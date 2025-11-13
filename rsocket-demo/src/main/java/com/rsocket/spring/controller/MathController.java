package com.rsocket.spring.controller;

import com.rsocket.spring.dto.Request;
import com.rsocket.spring.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Controller
@MessageMapping("math")
public class MathController {

    @MessageMapping("number")
    public Mono<Void> number(Mono<Request> requestMono) {
        return requestMono.doOnNext(r -> log.info("Number Request: {}", r))
                .then();
    }

    @MessageMapping("square")
    public Mono<Response> square(Mono<Request> requestMono) {
        return requestMono.doOnNext(r -> log.info("Square Request: {}", r))
                .map(r -> new Response(r.a() * r.a()));
    }

    @MessageMapping("table")
    public Flux<Response> table(Mono<Request> requestMono) {
        return requestMono.doOnNext(r -> log.info("Table Request: {}", r))
                .flatMapMany(r -> Flux.range(1, 10)
                        .delayElements(Duration.ofMillis(500))
                        .map(i -> new Response(i * r.a())));
    }

    @MessageMapping("cube")
    public Flux<Response> cube(Flux<Request> requestFlux) {
        return requestFlux
                .doOnNext(r -> log.info("Cube Request: {}", r))
                .map(r -> new Response(r.a() * r.a() * r.a()))
                .delayElements(Duration.ofMillis(500));
    }

    @MessageMapping("input.{num}")
    public Mono<Void> input(@DestinationVariable int num) {
        log.info("Input Request: {}", num);
        return Mono.empty();
    }

    @MessageExceptionHandler
    public Mono<Void> exception(Exception e) {
        return Mono.empty();
    }

}
