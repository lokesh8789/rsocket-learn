package com.rsocket.spring.client;

import com.rsocket.spring.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("peer.client")
@Slf4j
public class PeerToPeer {
    @MessageMapping("response")
    public Mono<Void> number(Mono<Response> requestMono) {
        return requestMono.doOnNext(r -> log.info("Number Response From Server: {}", r))
                .then();
    }
}
