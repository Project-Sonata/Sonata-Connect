package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Hooks;
import testing.shared.SonataTestHttpOperations;

import java.net.URI;
import java.time.Duration;

public class WebSocketPlayerEventConsumerTests extends AbstractIntegrationTest {

    @Autowired
    WebSocketClient webSocketClient;

    @Autowired
    SonataTestHttpOperations sonataHttpOperations;

    @LocalServerPort
    int port;


    final String VALID_ACCESS_TOKEN = "mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void prepare() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Test
    void shouldConnectToWs() {
        connectToWs(WebSocketSession::close);
    }

    private void connectToWs(WebSocketHandler handler) {
        String uriString = buildUri();

        webSocketClient.execute(URI.create(uriString), handler)
                .block(Duration.ofSeconds(5));
    }

    private String buildUri() {
        return String.format("ws://localhost:%s/v1/player/sync?token=%s", port, VALID_ACCESS_TOKEN);
    }
}
