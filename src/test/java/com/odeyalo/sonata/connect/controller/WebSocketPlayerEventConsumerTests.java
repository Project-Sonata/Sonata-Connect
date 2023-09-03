package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.PlayResumePlaybackRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import testing.faker.ConnectDeviceRequestFaker;
import testing.shared.SonataTestHttpOperations;
import testing.spring.autoconfigure.AutoConfigureSonataHttpClient;
import testing.spring.autoconfigure.AutoConfigureWebSocketClient;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.HttpHeaders.readOnlyHttpHeaders;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureWebSocketClient
@AutoConfigureSonataHttpClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class WebSocketPlayerEventConsumerTests {

    @Autowired
    WebSocketClient webSocketClient;

    @Autowired
    SonataTestHttpOperations sonataHttpOperations;

    @LocalServerPort
    int port;


    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void prepare() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Test
    void shouldConnectToWs() {
        connectToWs(WebSocketSession::close);
    }

    @Test
    @Disabled("THIS TEST WORKS RANDOM IDK HOW TO FIX IT")
    void shouldSendEventIfPlayCommandWasTriggered() {
        AtomicReference<List<String>> receivedMessages = new AtomicReference<>();

        URI wsUri = URI.create(buildUri());
        LinkedMultiValueMap<String, String> headers = enhanceHeaders();
        sonataHttpOperations.connectDevice(VALID_ACCESS_TOKEN, ConnectDeviceRequestFaker.create().get());

        webSocketClient.execute(wsUri,
                        readOnlyHttpHeaders(headers),
                        messageConsumer(1, receivedMessages))
                .and(Mono.fromRunnable(this::resumePlayback))
                .block(Duration.ofSeconds(5));

        assertThat(receivedMessages.get()).hasSize(1);
    }

    @Test
    @Disabled("Don't know how to consume messages from 2 different clients")
    void shouldSendEventIfPlayCommandWasTriggeredForSpecificRoom() {
        AtomicReference<List<String>> receivedMessagesClient1 = new AtomicReference<>();
        AtomicReference<List<String>> receivedMessagesClient2 = new AtomicReference<>();

        URI wsUri = URI.create(buildUri());
        LinkedMultiValueMap<String, String> headers = enhanceHeaders();
        sonataHttpOperations.connectDevice(VALID_ACCESS_TOKEN, ConnectDeviceRequestFaker.create().get());
//
        Mono.fromRunnable(() -> {
            System.out.println("Send message");
            resumePlayback();
            System.out.println("Sent message");
        }).and(
        webSocketClient.execute(wsUri,
                        readOnlyHttpHeaders(headers),
                        messageConsumer(1, receivedMessagesClient1)
                ).doOnSuccess(sub -> System.out.println("Сompleted subcriber 1"))
                .zipWith(webSocketClient.execute(wsUri,
                                readOnlyHttpHeaders(headers),
                                messageConsumer(1, receivedMessagesClient2))
                        .doOnSuccess(sub -> System.out.println("Сompleted subcriber 2"))))
                .block(Duration.ofSeconds(5));

        assertThat(receivedMessagesClient2.get()).hasSize(1);
        assertThat(receivedMessagesClient1.get()).hasSize(1);
    }

    @Test
    @Disabled("Disabled because it was written for example only and will be rewritten later")
    void shouldReceiveEvents() {

        Flux<String> input = Flux.just("Hello", "World", "I", "Love", "Miku");
        AtomicReference<List<String>> receivedMessagesClient1 = new AtomicReference<>();
        AtomicReference<List<String>> receivedMessagesClient2 = new AtomicReference<>();
        LinkedMultiValueMap<String, String> headers = enhanceHeaders();
        URI wsUri = URI.create(buildUri());
        // HTTP CALL TO PLAY -> UPDATE STATE AND NOTIFY SUBSCRIBERS

        sonataHttpOperations.connectDevice(VALID_ACCESS_TOKEN, ConnectDeviceRequestFaker.create().get());

        webSocketClient.execute(wsUri,
                        readOnlyHttpHeaders(headers),
                        messageConsumer(1, receivedMessagesClient1))

                .and(webSocketClient.execute(wsUri,
                        readOnlyHttpHeaders(headers),
                        messageConsumer(1, receivedMessagesClient2)))
                .and(Mono.fromRunnable(this::resumePlayback).then())
                .block(Duration.ofSeconds(5));

        assertThat(receivedMessagesClient1.get()).hasSize(5);
        assertThat(receivedMessagesClient2.get()).hasSize(5);
    }

    private void resumePlayback() {
        sonataHttpOperations.playOrResumePlayback(VALID_ACCESS_TOKEN, PlayResumePlaybackRequest.of("sonata:track:miku"));
        System.out.println("resumed playback");
    }

    @NotNull
    private static WebSocketHandler messageConsumer(int take, AtomicReference<List<String>> messageReference) {
        return (session) -> session.receive()
                .take(take)
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(payload -> System.out.println("REceived: " + payload + " " + session.getId()))
                .collectList()
                .doOnNext(messageReference::set)
                .then();
    }

    @NotNull
    private static WebSocketHandler messageProducer(Flux<String> input) {
        return (session) -> session.send(input.map(session::textMessage)
                .doOnNext(text -> System.out.println("Emitted data " + text)));
    }

    private void connectToWs(WebSocketHandler handler) {
        String uriString = buildUri();
        LinkedMultiValueMap<String, String> headers = enhanceHeaders();

        webSocketClient.execute(URI.create(uriString),
                        readOnlyHttpHeaders(headers),
                        handler)
                .block(Duration.ofSeconds(5));
    }

    @NotNull
    private LinkedMultiValueMap<String, String> enhanceHeaders() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN);
        return headers;
    }

    private String buildUri() {
        return String.format("ws://localhost:%s/v1/player/sync", port);
    }
}
