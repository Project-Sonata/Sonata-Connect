package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.PlayResumePlaybackRequest;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.dto.ReasonCodeAwareExceptionMessage;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.PlayerStateDtoAssert;
import testing.asserts.ReasonCodeAwareExceptionMessageAssert;
import testing.faker.ConnectDeviceRequestFaker;
import testing.shared.SonataTestHttpOperations;
import testing.spring.autoconfigure.AutoConfigureSonataHttpClient;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataHttpClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@ActiveProfiles("test")
class PauseCommandEndpointTest {
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    PlayerStateRepository playerStateRepository;
    @Autowired
    SonataTestHttpOperations sonataTestHttpOperations;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";


    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @AfterEach
    void cleanUp() {
        playerStateRepository.clear().block();
    }

    @Test
    void shouldReturn204StatusOnSuccess() {
        connectDevice();
        WebTestClient.ResponseSpec responseSpec = sendPauseRequest();

        responseSpec.expectStatus().isNoContent();
    }

    @Test
    void shouldPausePlayer() {
        connectDevice();
        WebTestClient.ResponseSpec ignored = sendPauseRequest();
        PlayerStateDto currentState = sonataTestHttpOperations.getCurrentState(VALID_ACCESS_TOKEN);

        PlayerStateDtoAssert.forState(currentState).shouldBePaused();
    }

    @Test
    void shouldReturnBadRequestErrorIfNoActiveDevice() {
        WebTestClient.ResponseSpec responseSpec = sendPauseRequest();

        responseSpec.expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnErrorReasonCode() {
        WebTestClient.ResponseSpec responseSpec = sendPauseRequest();

        ReasonCodeAwareExceptionMessage responseBody = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

        ReasonCodeAwareExceptionMessageAssert.forMessage(responseBody)
                .reasonCode().isEqualTo("no_active_device");
    }

    @Test
    void shouldReturnErrorDescription() {
        WebTestClient.ResponseSpec responseSpec = sendPauseRequest();

        ReasonCodeAwareExceptionMessage responseBody = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

        ReasonCodeAwareExceptionMessageAssert.forMessage(responseBody)
                .description().isEqualTo("Player command failed: No active device found");
    }

    private void connectDevice() {
        ConnectDeviceRequest connectDeviceRequest = ConnectDeviceRequestFaker.create().get();
        sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, connectDeviceRequest);
        sonataTestHttpOperations.playOrResumePlayback(VALID_ACCESS_TOKEN, PlayResumePlaybackRequest.of("sonata:track:miku"));
    }

    @NotNull
    private WebTestClient.ResponseSpec sendPauseRequest() {
        return webTestClient.put()
                .uri("/player/pause")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}
