package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.PlayResumePlaybackRequest;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.dto.ReasonCodeAwareExceptionMessage;
import com.odeyalo.sonata.connect.model.TrackItem;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.PredefinedPlayableItemLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.PlayerStateDtoAssert;
import testing.asserts.ReasonCodeAwareExceptionMessageAssert;
import testing.faker.ConnectDeviceRequestFaker;
import testing.faker.PlayableItemFaker.TrackItemFaker;
import testing.shared.SonataTestHttpOperations;
import testing.spring.autoconfigure.AutoConfigureSonataHttpClient;

import java.util.List;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureSonataHttpClient
public class PlayResumePlaybackEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerStateRepository playerStateRepository;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    SonataTestHttpOperations testOperations;

    final String EXISTING_TRACK_CONTEXT_URI = "sonata:track:cassie";
    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";


    @BeforeAll
    void prepare() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @AfterEach
    void afterEach() {
        this.playerStateRepository.clear().block();
    }

    @TestConfiguration
    static class Config {
        @Bean
        @Primary
        public PlayableItemLoader testablePlayableItemLoader() {
            final TrackItem trackItem = TrackItemFaker.create()
                    .withContextUri("sonata:track:cassie")
                    .get();

            return new PredefinedPlayableItemLoader(List.of(trackItem));
        }
    }


    @Test
    void shouldReturn204Status() {
        connectDevice();

        WebTestClient.ResponseSpec responseSpec = sendPlayOrResumeCommandRequest(
                PlayResumePlaybackRequest.of(EXISTING_TRACK_CONTEXT_URI)
        );

        responseSpec.expectStatus().isNoContent();
    }

    @Test
    void shouldChangePlayerCurrentPlayableItemToProvided() {
        connectDevice();

        final WebTestClient.ResponseSpec ignored = sendPlayOrResumeCommandRequest(
                PlayResumePlaybackRequest.of(EXISTING_TRACK_CONTEXT_URI)
        );

        final PlayerStateDto updatedState = getCurrentState();

        PlayerStateDtoAssert.forState(updatedState)
                .track().id().isEqualTo("cassie");
    }

    @Test
    void shouldSetTrueToPlayingField() {
        connectDevice();

        final WebTestClient.ResponseSpec ignored = sendPlayOrResumeCommandRequest(
                PlayResumePlaybackRequest.of(EXISTING_TRACK_CONTEXT_URI)
        );

        final PlayerStateDto updatedState = getCurrentState();

        PlayerStateDtoAssert.forState(updatedState)
                .shouldPlay();
    }

    @Test
    void shouldUpdateCurrentlyPlayingType() {
        connectDevice();

        final WebTestClient.ResponseSpec ignored = sendPlayOrResumeCommandRequest(
                PlayResumePlaybackRequest.of(EXISTING_TRACK_CONTEXT_URI)
        );

        final PlayerStateDto updatedState = getCurrentState();

        PlayerStateDtoAssert.forState(updatedState)
                .currentlyPlayingType().track();
    }

    @Test
    void shouldResumePlaybackIfBodyWasNotSetAndPlayerHasPlayableItem() {
        connectDevice();

        final WebTestClient.ResponseSpec ignored = sendPlayOrResumeCommandRequest(
                PlayResumePlaybackRequest.of(EXISTING_TRACK_CONTEXT_URI)
        );

        pausePlayback();


        sendResumeCurrentPlaybackRequest();

        final PlayerStateDto updatedState = getCurrentState();

        PlayerStateDtoAssert.forState(updatedState).shouldPlay();
    }

    @Test
    void shouldReturn400BadRequestIfContextUriMalformed() {
        connectDevice();

        final String invalidContextUri = "sonata:somethinginvalid:mikuuuu";

        final WebTestClient.ResponseSpec responseSpec = sendPlayOrResumeCommandRequest(PlayResumePlaybackRequest.of(invalidContextUri));

        responseSpec.expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnExceptionMessage() {
        connectDevice();

        final String invalidContextUri = "sonata:somethinginvalid:mikuuuu";

        final WebTestClient.ResponseSpec responseSpec = sendPlayOrResumeCommandRequest(PlayResumePlaybackRequest.of(invalidContextUri));

        final ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class)
                .returnResult().getResponseBody();

        ReasonCodeAwareExceptionMessageAssert.forMessage(body)
                .description().isEqualTo("Context uri is malformed");
    }

    @Test
    void shouldReturnExceptionReasonCode() {
        connectDevice();

        final String invalidContextUri = "sonata:somethinginvalid:mikuuuu";

        final WebTestClient.ResponseSpec responseSpec = sendPlayOrResumeCommandRequest(PlayResumePlaybackRequest.of(invalidContextUri));

        final ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

        ReasonCodeAwareExceptionMessageAssert.forMessage(body)
                .reasonCode().isEqualTo("malformed_context_uri");
    }

    @Test
    void shouldDoNothingIfPlayerWasJustCreated() {
        final PlayerStateDto beforeRequest = getCurrentState();

        final WebTestClient.ResponseSpec ignored = sendResumeCurrentPlaybackRequest();

        final PlayerStateDto afterRequest = getCurrentState();

        PlayerStateDtoAssert.forState(beforeRequest)
                .isEqualTo(afterRequest);
    }

    @Test
    void shouldReturn400StatusForEmptyState() {
        WebTestClient.ResponseSpec responseSpec = sendResumeCurrentPlaybackRequest();

        responseSpec.expectStatus().isBadRequest();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EmptyDeviceListTest {

        @Test
        void shouldReturnBadRequest() {

            final WebTestClient.ResponseSpec responseSpec = sendPlayOrResumeCommandRequest(
                    PlayResumePlaybackRequest.of(EXISTING_TRACK_CONTEXT_URI)
            );

            responseSpec.expectStatus().isBadRequest();
        }

        @Test
        void shouldReturnExceptionMessageInBody() {
            final WebTestClient.ResponseSpec responseSpec = sendPlayOrResumeCommandRequest(
                    PlayResumePlaybackRequest.of(EXISTING_TRACK_CONTEXT_URI)
            );

            final ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class)
                    .returnResult().getResponseBody();

            ReasonCodeAwareExceptionMessageAssert.forMessage(body)
                    .description().isEqualTo("Player command failed: No active device found");
        }

        @Test
        void shouldReturnExceptionReasonInBody() {

            final WebTestClient.ResponseSpec responseSpec = sendPlayOrResumeCommandRequest(
                    PlayResumePlaybackRequest.of(EXISTING_TRACK_CONTEXT_URI)
            );

            final ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

            ReasonCodeAwareExceptionMessageAssert.forMessage(body)
                    .reasonCode().isEqualTo("no_active_device");
        }

    }
    private void connectDevice() {
        final ConnectDeviceRequest connectDeviceRequest = ConnectDeviceRequestFaker.create().get();

        testOperations.connectDevice(VALID_ACCESS_TOKEN, connectDeviceRequest);
    }

    private void pausePlayback() {
        testOperations.pause(VALID_ACCESS_TOKEN);
    }

    @NotNull
    private WebTestClient.ResponseSpec sendResumeCurrentPlaybackRequest() {
        return sendPlayOrResumeCommandRequest(null);
    }

    @NotNull
    private WebTestClient.ResponseSpec sendPlayOrResumeCommandRequest(@Nullable PlayResumePlaybackRequest body) {

        final WebTestClient.RequestBodySpec builder = webTestClient.put()
                .uri("/player/play")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN);

        if (body != null) {
            builder
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        return builder.exchange();
    }

    @NotNull
    private PlayerStateDto getCurrentState() {
        return testOperations.getCurrentState(VALID_ACCESS_TOKEN);
    }
}
