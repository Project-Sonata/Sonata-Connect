package com.odeyalo.sonata.connect.controller;


import com.odeyalo.sonata.connect.AbstractIntegrationTest;
import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.PlayResumePlaybackRequest;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.dto.ReasonCodeAwareExceptionMessage;
import com.odeyalo.sonata.connect.model.PlayableItemDuration;
import com.odeyalo.sonata.connect.model.TrackItem;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.PredefinedPlayableItemLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.faker.ConnectDeviceRequestFaker;
import testing.faker.PlayableItemFaker;
import testing.shared.SonataTestHttpOperations;
import testing.spring.callback.ClearPlayerState;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ClearPlayerState
class SeekPositionEndpointTest extends AbstractIntegrationTest {


    @Autowired
    WebTestClient webClient;

    @Autowired
    SonataTestHttpOperations sonataTestHttpOperations;

    static final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    static final String PLAYABLE_ITEM_CONTEXT_URI = "sonata:track:cassie";


    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @TestConfiguration
    static class Config {
        @Bean
        @Primary
        public PlayableItemLoader testablePlayableItemLoader() {
            final TrackItem trackItem = PlayableItemFaker.TrackItemFaker.create()
                    .withDuration(PlayableItemDuration.ofSeconds(180))
                    .withContextUri(PLAYABLE_ITEM_CONTEXT_URI)
                    .get();

            return new PredefinedPlayableItemLoader(List.of(trackItem));
        }
    }

    @Test
    void shouldReturn204StatusAfterCommandSuccess() {
        connectDevice();

        startPlayTrack();

        final WebTestClient.ResponseSpec responseSpec = seekPositionRequest(100, "millis");

        responseSpec.expectStatus().isNoContent();
    }

    @Test
    void shouldUpdateCurrentPlayerState() {
        connectDevice();

        startPlayTrack();

        final WebTestClient.ResponseSpec ignored = seekPositionRequest(10_000, "millis");

        final PlayerStateDto currentState = sonataTestHttpOperations.getCurrentState(VALID_ACCESS_TOKEN);

        assertThat(currentState.getProgressMs()).isGreaterThanOrEqualTo(10_000);
    }

    @Test
    void shouldProperlyUpdatePositionIfSecondPrecisionIsUsed() {
        connectDevice();

        startPlayTrack();

        final WebTestClient.ResponseSpec ignored = seekPositionRequest(20, "seconds");

        final PlayerStateDto currentState = sonataTestHttpOperations.getCurrentState(VALID_ACCESS_TOKEN);

        assertThat(currentState.getProgressMs()).isGreaterThanOrEqualTo(20_000);
    }

    @Test
    void shouldReturnErrorIfInvalidPrecisionIsUsed() {
        connectDevice();

        startPlayTrack();

        final WebTestClient.ResponseSpec responseSpec = seekPositionRequest(20, "invalid");

        responseSpec.expectStatus().isBadRequest();

        responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class)
                .value(message -> assertThat(message.getReasonCode()).isEqualTo("unsupported_precision"))
                .value(message -> assertThat(message.getDescription()).isEqualTo("Player command error: unsupported precision used. Supported case insensitive: MILLIS, SECONDS."));
    }

    @Test
    void shouldReturnErrorIfNothingIsPlaying() {
        connectDevice();

        final WebTestClient.ResponseSpec responseSpec = seekPositionRequest(10_000, "millis");

        responseSpec.expectStatus().isBadRequest();

        responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class)
                .value(message -> assertThat(message.getReasonCode()).isEqualTo("playable_item_required"))
                .value(message -> assertThat(message.getDescription()).isEqualTo("Player command error: no item is playing"));
    }

    @Test
    void shouldReturnErrorIfPositionIsNegative() {
        connectDevice();

        startPlayTrack();

        final WebTestClient.ResponseSpec responseSpec = seekPositionRequest(-10, "millis");

        responseSpec.expectStatus().isBadRequest();

        responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class)
                .value(message -> assertThat(message.getReasonCode()).isEqualTo("invalid_position"))
                .value(message -> assertThat(message.getDescription()).isEqualTo("Player command error: position must be positive"));
    }

    @Test
    void shouldReturnErrorIfPositionIsGreaterThanTrackDuration() {
        connectDevice();

        startPlayTrack();

        WebTestClient.ResponseSpec responseSpec = seekPositionRequest(Integer.MAX_VALUE, "millis");

        responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class)
                .value(message -> assertThat(message.getReasonCode()).isEqualTo("seek_position_exceed"))
                .value(message -> assertThat(message.getDescription()).isEqualTo("Player command error: position cannot be greater than track duration"));
    }

    private WebTestClient.ResponseSpec seekPositionRequest(final int position, final String precision) {
        return webClient.put().uri(b -> b.path("/player/seek")
                        .queryParam("position", position)
                        .queryParam("precision", precision)
                        .build())
                .header(AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }

    private void connectDevice() {
        final ConnectDeviceRequest connectDeviceRequest = ConnectDeviceRequestFaker.create().get();

        sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, connectDeviceRequest);
    }

    private void startPlayTrack() {
        sonataTestHttpOperations.playOrResumePlayback(VALID_ACCESS_TOKEN, PlayResumePlaybackRequest.of(PLAYABLE_ITEM_CONTEXT_URI));
    }
}
