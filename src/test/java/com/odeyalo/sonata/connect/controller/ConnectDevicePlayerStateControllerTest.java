package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.PlayerStateDtoAssert;
import testing.faker.ConnectDeviceRequestFaker;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class ConnectDevicePlayerStateControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerStateStorage playerStateStorage;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @AfterEach
    void afterEach() {
        playerStateStorage.clear().block();
    }

    @Test
    void shouldReturnNoContentStatusCode() {
        WebTestClient.ResponseSpec exchange = prepareValidAndSend();

        exchange.expectStatus().isNoContent();
    }

    @Test
    void shouldReturnApplicationJson() {
        WebTestClient.ResponseSpec exchange = prepareValidAndSend();

        exchange.expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void deviceShouldBeAdded() {
        ConnectDeviceRequest body = ConnectDeviceRequestFaker.create().get();

        WebTestClient.ResponseSpec responseSpec = sendRequest(body);

        PlayerStateDto afterRequest = getCurrentPlayerState();

        PlayerStateDtoAssert.forState(afterRequest)
                .devices().length(1);
    }

    @Test
    void shouldUpdateStateWithValidDeviceInfo() {
        ConnectDeviceRequest body = ConnectDeviceRequestFaker.create().get();

        WebTestClient.ResponseSpec responseSpec = sendRequest(body);

        PlayerStateDto afterRequest = getCurrentPlayerState();

        PlayerStateDtoAssert.forState(afterRequest)
                .devices().peekFirst()
                .id(body.getId())
                .name(body.getName())
                .volume(body.getVolume())
                .type(body.getDeviceType());
    }

    @NotNull
    private WebTestClient.ResponseSpec prepareValidAndSend() {
        ConnectDeviceRequest body = ConnectDeviceRequestFaker.create().get();
        return sendRequest(body);
    }

    private PlayerStateDto getCurrentPlayerState() {
        return webTestClient.get()
                .uri("/player/state")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange().expectBody(PlayerStateDto.class)
                .returnResult().getResponseBody();

    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequest(ConnectDeviceRequest connectDeviceRequest) {
        return webTestClient.put()
                .uri("/player/device/connect")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(connectDeviceRequest)
                .exchange();
    }
}
