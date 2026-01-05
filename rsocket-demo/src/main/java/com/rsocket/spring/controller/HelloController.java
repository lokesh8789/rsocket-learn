package com.rsocket.spring.controller;

import com.rsocket.spring.dto.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("hello")
public class HelloController {

    @MessageMapping("test")
    public Mono<String> test(@Payload Mono<User> message) {
        return Mono.just("hello")
                .zipWith(message, (s, s2) -> s + " " + s2);
    }

    @MessageMapping("print")
    public Mono<User> print(@Payload Mono<String> message) {
        return Mono.just("hello")
                .zipWith(message, (s, s2) -> new User(s2, 23));
    }

}