package com.rsocket.spring.client;

import com.rsocket.spring.dto.ConnectionDto;
import com.rsocket.spring.dto.Request;
import io.rsocket.core.Resume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Component
public class ConnectionMappingClient implements CommandLineRunner {

    private final RSocketRequester rSocketRequester;

    public ConnectionMappingClient(RSocketRequester.Builder builder) {
        this.rSocketRequester = builder
                .setupData(new ConnectionDto("setup"))
                .rsocketConnector(connector -> connector
                        .resume(new Resume().retry(Retry.fixedDelay(5000, Duration.ofSeconds(2))))
                        .reconnect(Retry.max(3))
                )
                .tcp("localhost", 6565);
    }

    public void number() {
        rSocketRequester.route("math.number")
                .data(new Request(23))
                .send()
                .subscribe();
    }


    @Override
    public void run(String... args) throws Exception {
        number();
//        Thread.sleep(5_000);
//        number();
    }
}
