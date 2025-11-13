package com.rsocket.spring.controller;

import com.rsocket.spring.dto.Request;
import com.rsocket.spring.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Controller
@MessageMapping("peer.server")
public class PeerToPeerServer {

    @MessageMapping("request")
    public Mono<Void> number(Mono<Request> requestMono, RSocketRequester rSocketRequester) {
        requestMono
                .doOnNext(r -> log.info("Received request {}", r))
                .delayElement(Duration.ofSeconds(2))
                .flatMap(r -> rSocketRequester.route("peer.client.response")
                        .data(new Response(r.a() * 10))
                        .send())
                .subscribe();
        return Mono.empty();
    }
}
