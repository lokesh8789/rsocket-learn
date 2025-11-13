package com.rsocket.spring.guessgame;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class Client implements CommandLineRunner {

    private final RSocketRequester rSocketRequester;
    public Client(RSocketRequester.Builder builder) {
        this.rSocketRequester = builder.tcp("localhost", 6565);
    }


    public void guess() {
        Player player = new Player();
        rSocketRequester.route("guess")
                .data(player.getFlux().delayElements(Duration.ofSeconds(1)))
                .retrieveFlux(GuessResult.class)
                .doOnNext(player::handleResponse)
                .doFirst(player::startPlay)
                .subscribe();
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Guessing Game");
        Thread.sleep(3000);
        guess();
    }

}
