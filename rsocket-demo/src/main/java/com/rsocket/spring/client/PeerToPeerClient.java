package com.rsocket.spring.client;

import com.rsocket.spring.dto.Request;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Component;

@Component
public class PeerToPeerClient implements CommandLineRunner {

    private final RSocketMessageHandler rSocketMessageHandler;
    private final RSocketRequester rSocketRequester;
    public PeerToPeerClient(RSocketMessageHandler rSocketMessageHandler, RSocketRequester.Builder builder) {
        this.rSocketMessageHandler = rSocketMessageHandler;
        this.rSocketRequester = builder
                .rsocketConnector(connector -> connector.acceptor(rSocketMessageHandler.responder()))
                .tcp("localhost", 6565);
    }

    public void request() {
        rSocketRequester.route("peer.server.request")
                .data(new Request(23))
                .send()
                .subscribe();
    }
    @Override
    public void run(String... args) throws Exception {
//        request();
    }
}
