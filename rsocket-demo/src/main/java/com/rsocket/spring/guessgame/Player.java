package com.rsocket.spring.guessgame;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
public class Player {
    private int l = 0;
    private int m = 0;
    private int u = 100;
    private int attempt = 0;

    private final Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();

    public Flux<Integer> getFlux() {
        return sink.asFlux();
    }

    public void handleResponse(GuessResult guessResult) {
        attempt++;
        log.info("Attempt {} And Mid: {}", attempt, m);

        if (guessResult.equals(GuessResult.EQUAL)) {
            sink.tryEmitComplete();
            return;
        }

        if (guessResult.equals(GuessResult.GREATER)) {
            u = m;
        } else if (guessResult.equals(GuessResult.LESSER)) {
            l = m;
        }
        nextNum();
    }

    private void nextNum() {
        m = l + (u - l) / 2;
        sink.tryEmitNext(m);
    }

    public void startPlay() {
        nextNum();
    }

}
