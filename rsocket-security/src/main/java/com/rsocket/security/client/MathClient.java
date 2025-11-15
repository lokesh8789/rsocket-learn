package com.rsocket.security.client;

import com.rsocket.security.dto.LoginRequest;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class MathClient implements CommandLineRunner {

    private final RSocketRequester rSocketRequester;
    private final MimeType mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());

    public MathClient(RSocketRequester.Builder builder) {
        UsernamePasswordMetadata metadata = new UsernamePasswordMetadata("user", "user");

        this.rSocketRequester = builder
//                .setupMetadata(metadata, mimeType)
                .tcp("localhost", 6566);
    }

    // Simple Authentication Way
    /*public void square() {
        UsernamePasswordMetadata metadata = new UsernamePasswordMetadata("user", "user");
        rSocketRequester.route("math.square")
                .metadata(metadata, mimeType) // Can be used for Request Level Auth
                .data(5)
                .retrieveMono(Integer.class)
                .doOnNext(response -> log.info("Square Response: {}", response))
                .subscribe();
    }*/

    // JWT Way
    public void square(String token) {
        UsernamePasswordMetadata usernamePasswordMetadata = new UsernamePasswordMetadata("", token);
        rSocketRequester.route("math.square")
                .metadata(usernamePasswordMetadata, mimeType)
                .metadata(UUID.randomUUID(), MimeTypeUtils.parseMimeType(WellKnownMimeType.APPLICATION_CBOR.getString())) // Custom MetaData To be Extracted via @Header(".....")
                .data(5)
                .retrieveMono(Integer.class)
                .doOnNext(response -> log.info("Square Response: {}", response))
                .subscribe();
    }

    public void cube() {
        rSocketRequester.route("math.cube")
                .data(5)
                .retrieveMono(Integer.class)
                .doOnNext(response -> log.info("Cube Response: {}", response))
                .subscribe();
    }

    public Mono<String> login() {
        return rSocketRequester.route("math.login")
                .data(new LoginRequest("user", "user"))
                .retrieveMono(String.class)
                .doOnNext(response -> log.info("Login Response: {}", response));
    }

    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(5_000);
        log.info("Other Microservice Math Client");
//        square();
//        cube();
        login()
                .doOnNext(this::square)
                .subscribe();
    }
}
