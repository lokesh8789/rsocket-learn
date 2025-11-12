package com.lokesh.server;

import com.lokesh.dto.RequestDto;
import com.lokesh.dto.ResponseDto;
import com.lokesh.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class PeerToPeerService implements RSocket {

    private final RSocket clientRSocket;

    public PeerToPeerService(RSocket clientRSocket) {
        this.clientRSocket = clientRSocket;
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        RequestDto requestDto = ObjectUtil.toObject(payload, RequestDto.class);
        System.out.println("requestDto = " + requestDto);
        Mono.justOrEmpty(requestDto)
                .delayElement(Duration.ofSeconds(4))
                .doOnNext(a -> System.out.println("Emitting To Client"))
                .flatMap(this::findCube)
                .subscribe();
        return Mono.empty();
    }

    private Mono<Void> findCube(RequestDto requestDto) {
        int input = requestDto.input();
        ResponseDto responseDto = new ResponseDto(input, input * input * input);
        Payload payload = ObjectUtil.toPayload(responseDto);
        return clientRSocket.fireAndForget(payload);
    }
}
