package com.rsocket.spring.client;

import com.rsocket.spring.dto.Request;
import com.rsocket.spring.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class MathClient implements CommandLineRunner {

    private final RSocketRequester rSocketRequester;
    public MathClient(RSocketRequester.Builder builder) {
        this.rSocketRequester = builder
                .tcp("localhost", 6565);
    }

    public void number() {
        rSocketRequester.route("math.number")
                .data(new Request(23))
                .send()
                .subscribe();
    }

    public void square() {
        rSocketRequester.route("math.square")
                .data(new Request(5))
                .retrieveMono(Response.class)
                .doOnNext(response -> log.info("Square Response: {}", response))
                .subscribe();
    }

    public void table() {
        rSocketRequester.route("math.table")
                .data(new Request(4))
                .retrieveFlux(Response.class)
                .doOnNext(response -> log.info("Table Response: {}", response))
                .subscribe();
    }

    public void cube() {
        rSocketRequester.route("math.cube")
                .data(Flux.range(1,9)
                        .map(Request::new))
                .retrieveFlux(Response.class)
                .doOnNext(response -> log.info("Cube Response: {}", response))
                .subscribe();
    }

    public void input() {
        rSocketRequester.route("math.input.{num}", 55)
                .send()
                .subscribe();
    }

    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(5_000);
        log.info("MathClient start");
//        number();
//        square();
//        table();
//        cube();
        input();
    }
}
