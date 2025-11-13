package com.rsocket.spring.guessgame;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Controller
public class Server {

    @MessageMapping("guess")
    public Flux<GuessResult> guess(Flux<Integer> requestFlux) {
        int num = ThreadLocalRandom.current().nextInt(1, 100);
        log.info("Guess Number is {}", num);
        return requestFlux
                .map(i -> compare(i, num));
    }

    private GuessResult compare(int i, int num) {
        if(i == num) {
            return GuessResult.EQUAL;
        } else if(i > num) {
            return GuessResult.GREATER;
        } else {
            return GuessResult.LESSER;
        }
    }

}
