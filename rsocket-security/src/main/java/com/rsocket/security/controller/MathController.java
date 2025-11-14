package com.rsocket.security.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@MessageMapping("math")
public class MathController {

    @MessageMapping("square")
    public Mono<Integer> square(@AuthenticationPrincipal UserDetails userDetails, Mono<Integer> requestMono) {
        return requestMono.doOnNext(r -> log.info("Square Request: {} For User: {}", r, userDetails))
                .map(r -> r * r);
    }

    @MessageMapping("cube")
    public Mono<Integer> cube(Mono<Integer> requestMono) {
        return requestMono.doOnNext(r -> log.info("Cube Request: {}", r))
                .map(r -> r * r * r);
    }

}
