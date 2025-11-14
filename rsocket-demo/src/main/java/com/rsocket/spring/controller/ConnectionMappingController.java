package com.rsocket.spring.controller;

import com.rsocket.spring.dto.ConnectionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
public class ConnectionMappingController {

    /*@ConnectMapping
    public Mono<Void> connect(ConnectionDto request, RSocketRequester rSocketRequester) {
        log.info("Connecting to Server: {}", request);
        Mono.just(request)
                .delayElement(Duration.ofSeconds(3))
                .doOnNext(connectionDto -> rSocketRequester.dispose())
                .subscribe();
        return Mono.empty();
    }*/

    private final Set<RSocketRequester> set = new HashSet<>();

    private void add(RSocketRequester rsocketRequester) {
        rsocketRequester.rsocket()
                .onClose()
                .doFirst(() -> set.add(rsocketRequester))
                .doFinally(signalType -> set.remove(rsocketRequester))
                .subscribe();
    }

    @ConnectMapping
    public Mono<Void> connect(RSocketRequester rSocketRequester) {
        log.info("Connecting to Server");
        return Mono.fromRunnable(() -> add(rSocketRequester));
    }

    @ConnectMapping("connect.route")
    public Mono<Void> connect2(RSocketRequester rSocketRequester) {
        log.info("Connecting to connect.route");
        return Mono.empty();
    }

//    @Scheduled(fixedRate = 1000)
    public void scheduled() {
        log.info("{}", set);
    }
}
