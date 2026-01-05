package com.rsocket.spring.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.rsocket.spring.dto.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.metadata.CompositeMetadataCodec;
import io.rsocket.metadata.TaggingMetadataCodec;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;

@Component
@Slf4j
public class HelloClient implements CommandLineRunner {
    private final RSocketRequester rSocketRequester;
    private final RSocketClient rSocketClient;

    public HelloClient(RSocketRequester.Builder builder) {
        this.rSocketRequester = builder
                .rsocketStrategies(builder1 -> builder1.encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
                        .decoders(decoders -> decoders.add(new Jackson2CborDecoder())))
                .tcp("localhost", 6565);

        Mono<RSocket> rSocketMono = RSocketConnector.create()
                .dataMimeType(WellKnownMimeType.APPLICATION_CBOR.getString())
                .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString())
                .connect(TcpClientTransport.create("localhost", 6565));

        this.rSocketClient = RSocketClient.from(rSocketMono);
    }

    private Payload createPayload(byte[] bytes, String routeName) {
        ByteBuf routeMetadata = TaggingMetadataCodec.createTaggingContent(ByteBufAllocator.DEFAULT, Collections.singletonList(routeName));
        CompositeByteBuf compositeMetadata = ByteBufAllocator.DEFAULT.compositeBuffer();
        CompositeMetadataCodec.encodeAndAddMetadata(
                compositeMetadata,
                ByteBufAllocator.DEFAULT,
                WellKnownMimeType.MESSAGE_RSOCKET_ROUTING,
                routeMetadata
        );
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        return DefaultPayload.create(byteBuf, compositeMetadata);
    }

    public Mono<String> send(User data) {
        return rSocketRequester.route("hello.test")
                .data(data)
                .retrieveMono(String.class);
    }

    public Mono<String> sendNative(User data) {
        return rSocketClient.requestResponse(Mono.defer(() -> {
                    try {
                        return Mono.just(createPayload(new ObjectMapper(new CBORFactory()).writeValueAsBytes(data), "hello.test"));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .map(Payload::getDataUtf8);
    }

    public Mono<User> sendPrint(String data) {
        return rSocketRequester.route("hello.print")
                .data(data)
                .retrieveMono(User.class);
    }

    public Mono<User> sendPrintNative(String data) {
        return rSocketClient.requestResponse(Mono.defer(() -> Mono.just(createPayload(data.getBytes(StandardCharsets.UTF_8), "hello.print"))))
                .map(Payload::getData)
                .map(byteBuffer -> {
                    try {
                        return new ObjectMapper(new CBORFactory()).readValue(byteBuffer.array(), User.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Rsocket Demo...");
        Mono.delay(Duration.ofSeconds(5))
                .flatMap(aLong -> Mono.zip(
                                send(new User("Lokesh", 24))
                                        .doOnSuccess(s -> log.info("{}", s)),
                                sendNative(new User("Lokesh", 24))
                                        .doOnSuccess(s -> log.info("{}", s)),
                                sendPrint("Lokesh")
                                        .doOnSuccess(s -> log.info("{}", s)),
                                sendPrintNative("Lokesh")
                                        .doOnSuccess(s -> log.info("{}", s))
                        )
                ).subscribe();
    }
}
