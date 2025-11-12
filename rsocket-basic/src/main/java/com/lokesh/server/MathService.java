package com.lokesh.server;

import com.lokesh.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class MathService implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("MathService fireAndForget ");
        System.out.println("payload = "  + ObjectUtil.toObject(payload, Object.class));
        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        System.out.println("MathService requestResponse ");
        System.out.println("payload = "  + ObjectUtil.toObject(payload, Object.class));
        return Mono.fromCallable(() -> payload)
                .delayElement(Duration.ofSeconds(1));
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        System.out.println("MathService requestStream ");
        System.out.println("payload = "  + ObjectUtil.toObject(payload, Object.class));
        return Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .map(aLong -> payload);
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        System.out.println("MathService requestChannel ");
        return Flux.from(payloads)
                .doOnNext(payload -> System.out.println("payload = "  + ObjectUtil.toObject(payload, Object.class)))
                .delayElements(Duration.ofSeconds(1));
    }
}
