package com.lokesh.server;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import reactor.core.publisher.Mono;

public class SocketAcceptorImpl implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload connectionSetupPayload, RSocket rSocket) {
        System.out.println("Accepting RSocket");
//        return Mono.fromCallable(MathService::new);
//        return Mono.fromCallable(() -> new PeerToPeerService(rSocket));
        return Mono.fromCallable(FastProducerService::new);
    }
}
