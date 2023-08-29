package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.PlayResumePlaybackRequest;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.dto.ReasonCodeAwareExceptionMessage;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
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
import testing.asserts.ReasonCodeAwareExceptionMessageAssert;
import testing.faker.ConnectDeviceRequestFaker;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class PlayResumeEndpointPlayerStateControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerStateStorage playerStateStorage;


    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void prepare() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @AfterEach
    void afterEach() {
        this.playerStateStorage.clear().block();
    }

    @Test
    void shouldReturn204StatusWithBody() {
        connectDevice();

        String trackUri = "sonata:track:cassie";

        WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest(PlayResumePlaybackRequest.of(trackUri));

        responseSpec.expectStatus().isNoContent();
    }

    @Test
    void shouldChangePlayerCurrentPlayableItemToProvided() {
        connectDevice();

        String trackUri = "sonata:track:cassie";

        WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest(PlayResumePlaybackRequest.of(trackUri));

        PlayerStateDto updatedState = getCurrentState();

        PlayerStateDtoAssert.forState(updatedState)
                .track().id().isEqualTo("cassie");
    }

    @Test
    void shouldReturn400BadRequestIfContextUriMalformed() {
        connectDevice();
        String invalidContextUri = "sonata:somethinginvalid:mikuuuu";

        WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest(PlayResumePlaybackRequest.of(invalidContextUri));

        responseSpec.expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnExceptionMessage() {
        connectDevice();

        String invalidContextUri = "sonata:somethinginvalid:mikuuuu";

        WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest(PlayResumePlaybackRequest.of(invalidContextUri));

        ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

        ReasonCodeAwareExceptionMessageAssert.forMessage(body)
                .description().isEqualTo("Context uri is malformed");
    }

    @Test
    void shouldReturnExceptionReasonCode() {
        connectDevice();

        String invalidContextUri = "sonata:somethinginvalid:mikuuuu";

        WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest(PlayResumePlaybackRequest.of(invalidContextUri));

        ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

        ReasonCodeAwareExceptionMessageAssert.forMessage(body)
                .reasonCode().isEqualTo("malformed_context_uri");
    }

    @Test
    void shouldDoNothingIfPlayerWasJustCreated() {
        PlayerStateDto beforeRequest = getCurrentState();

        WebTestClient.ResponseSpec responseSpec = sendResumeCurrentPlaybackEndpointRequest();

        PlayerStateDto afterRequest = getCurrentState();

        PlayerStateDtoAssert.forState(beforeRequest)
                .isEqualTo(afterRequest);
    }

    @Test
    void shouldReturn400StatusForEmptyState() {
        WebTestClient.ResponseSpec responseSpec = sendResumeCurrentPlaybackEndpointRequest();

        responseSpec.expectStatus().isBadRequest();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EmptyDeviceListTests {

        @Test
        void shouldReturnBadRequest() {
            String trackUri = "sonata:track:cassie";

            WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest(PlayResumePlaybackRequest.of(trackUri));

            responseSpec.expectStatus().isBadRequest();
        }

        @Test
        void shouldReturnExceptionMessageInBody() {
            String trackUri = "sonata:track:cassie";

            WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest(PlayResumePlaybackRequest.of(trackUri));

            ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

            ReasonCodeAwareExceptionMessageAssert.forMessage(body)
                    .description().isEqualTo("There is no active device");
        }

        @Test
        void shouldReturnExceptionReasonInBody() {
            String trackUri = "sonata:track:cassie";

            WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest(PlayResumePlaybackRequest.of(trackUri));

            ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

            ReasonCodeAwareExceptionMessageAssert.forMessage(body)
                    .reasonCode().isEqualTo("no_active_device");
        }
    }

    @NotNull
    private ConnectDeviceRequest connectDevice() {
        ConnectDeviceRequest connectDeviceRequest = ConnectDeviceRequestFaker.create().get();
        WebTestClient.ResponseSpec response = webTestClient.put()
                .uri("/player/device/connect")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(connectDeviceRequest)
                .exchange();
        return connectDeviceRequest;
    }

    @NotNull
    private WebTestClient.ResponseSpec sendResumeCurrentPlaybackEndpointRequest() {
        return webTestClient.put()
                .uri("/player/play")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }

    @NotNull
    private WebTestClient.ResponseSpec sendResumeEndpointRequest(PlayResumePlaybackRequest body) {
        return webTestClient.put()
                .uri("/player/play")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    private PlayerStateDto getCurrentState() {
        return webTestClient.get()
                .uri("/player/state")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange()
                .expectBody(PlayerStateDto.class)
                .returnResult()
                .getResponseBody();
    }
}
