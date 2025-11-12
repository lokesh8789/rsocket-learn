package com.lokesh.client;

import com.lokesh.dto.RequestDto;
import com.lokesh.dto.ResponseDto;
import com.lokesh.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import reactor.core.publisher.Mono;

public class PeerToPeerClient implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("Client received payload: " + ObjectUtil.toObject(payload, ResponseDto.class));
        return Mono.empty();
    }
}
