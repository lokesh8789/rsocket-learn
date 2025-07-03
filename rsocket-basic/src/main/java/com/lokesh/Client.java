package com.lokesh;

import com.lokesh.dto.RequestDto;
import com.lokesh.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.io.Flushable;
import java.time.Duration;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        RSocket rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 6565))
                .block();
        
        assert rSocket != null;
//        fireAndForget(rSocket);
//        requestResponse(rSocket);
//        requestStream(rSocket);
        requestChannel(rSocket);
    }

    private static void requestChannel(RSocket rSocket) throws InterruptedException {
        Flux<Payload> payloadFlux = Flux.range(1, 10)
                .map(i -> ObjectUtil.toPayload(new RequestDto(i)))
                .delayElements(Duration.ofSeconds(1));
        rSocket.requestChannel(payloadFlux)
                .take(5)
                .subscribe(payload -> System.out.println(ObjectUtil.toObject(payload, RequestDto.class)));

        Thread.sleep(Duration.ofSeconds(15));
    }

    private static void requestStream(RSocket rSocket) throws InterruptedException {
        rSocket.requestStream(ObjectUtil.toPayload(new RequestDto(242)))
                .take(5)
                .subscribe(payload -> System.out.println(ObjectUtil.toObject(payload, RequestDto.class)));

        Thread.sleep(Duration.ofSeconds(15));
    }

    private static void requestResponse(RSocket rSocket) throws InterruptedException {
        rSocket.requestResponse(ObjectUtil.toPayload(new RequestDto(242)))
                .subscribe(payload -> System.out.println(ObjectUtil.toObject(payload, RequestDto.class)));

        Flux.range(1, 10)
                .concatMap(i -> rSocket.requestResponse(ObjectUtil.toPayload(new RequestDto(22))))
                .subscribe(payload ->  System.out.println(ObjectUtil.toObject(payload, RequestDto.class)));

        Thread.sleep(Duration.ofSeconds(10));
    }

    private static void fireAndForget(RSocket rSocket) throws InterruptedException {
        rSocket.fireAndForget(ObjectUtil.toPayload(new RequestDto(22)))
                .subscribe();

        rSocket.fireAndForget(ObjectUtil.toPayload(new RequestDto(23))).subscribe();
        Thread.sleep(5000);
    }
}
