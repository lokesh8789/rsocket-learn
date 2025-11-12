package com.lokesh.client;

import com.lokesh.dto.RequestDto;
import com.lokesh.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class BackpressureClient {

    public static void main(String[] args) throws InterruptedException {
//        RSocket rSocket = RSocketConnector.create()
//                .connect(TcpClientTransport.create("localhost", 6565))
//                .block();
//
//        assert rSocket != null;
//        requestStream(rSocket);

        Mono<RSocket> socketMono = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 6565));

        RSocketClient rSocketClient = RSocketClient.from(socketMono);
        requestStream(rSocketClient);
    }

    private static void requestStream(RSocket rSocket) throws InterruptedException {
        rSocket.requestStream(DefaultPayload.create(""))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .delayElements(Duration.ofMillis(300))
                .take(10)
                .blockLast();

//        Thread.sleep(Duration.ofSeconds(10));
//
//        rSocket.requestStream(DefaultPayload.create(""))
//                .map(Payload::getDataUtf8)
//                .doOnNext(System.out::println)
//                .delayElements(Duration.ofMillis(300))
//                .take(10)
//                .blockLast();
        // Above commented one throws this exception if try to reconnect if server rerun,
        // to fix this use RsocketClient instead of Rsocket.
        // Exception in thread "main" reactor.core.Exceptions$ReactiveException: java.nio.channels.ClosedChannelException
    }

    private static void requestStream(RSocketClient rSocketClient) throws InterruptedException {
        rSocketClient.requestStream(Mono.just(DefaultPayload.create("")))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .delayElements(Duration.ofMillis(300))
                .take(10)
                .blockLast();

        Thread.sleep(Duration.ofSeconds(10));

        rSocketClient.requestStream(Mono.just(DefaultPayload.create("")))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .delayElements(Duration.ofMillis(300))
                .take(10)
                .blockLast();
    }
}
