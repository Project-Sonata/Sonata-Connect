package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.AbstractIntegrationTest;
import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.dto.ReasonCodeAwareExceptionMessage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.faker.ConnectDeviceRequestFaker;
import testing.shared.SonataTestHttpOperations;
import testing.spring.callback.ClearPlayerState;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ClearPlayerState
class ChangePlayerVolumeEndpointTest extends AbstractIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataTestHttpOperations sonataTestHttpOperations;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 20, 40, 50, 60, 100})
    void shouldReturn204NoContentStatusAsResponseForValidVolume(final int volume) {
        connectDevice();

        final WebTestClient.ResponseSpec responseSpec = sendChangeVolumeRequest(volume);

        responseSpec.expectStatus().isNoContent();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -20, -100, -1000})
    void shouldReturn404BadRequestStatusCodeIfVolumeIsNegative(final int volume) {
        connectDevice();

        final WebTestClient.ResponseSpec responseSpec = sendChangeVolumeRequest(volume);

        responseSpec.expectStatus().isBadRequest();

        responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class)
                .value(message -> assertThat(message.getReasonCode()).isEqualTo("invalid_volume"));
    }

    @ParameterizedTest
    @ValueSource(ints = {101, 200, 300, 500})
    void shouldReturn404BadRequestStatusCodeIfVolumeIsGreaterThan100(final int volume) {
        connectDevice();

        final WebTestClient.ResponseSpec responseSpec = sendChangeVolumeRequest(volume);

        responseSpec.expectStatus().isBadRequest();

        responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class)
                .value(message -> assertThat(message.getReasonCode()).isEqualTo("invalid_volume"));
    }

    @Test
    void shouldUpdatePlayerStateAfterCommand() {
        connectDevice();

        sendChangeVolumeRequest(30);

        final PlayerStateDto currentState = sonataTestHttpOperations.getCurrentState(VALID_ACCESS_TOKEN);

        assertThat(currentState.getVolume()).isEqualTo(30);
    }

    @Test
    void shouldReturn400BadRequestIfNoDeviceIsConnected() {

        final WebTestClient.ResponseSpec responseSpec = sendChangeVolumeRequest(30);

        responseSpec
                .expectStatus().isBadRequest()
                .expectBody(ReasonCodeAwareExceptionMessage.class)
                .value(message -> assertThat(message.getReasonCode()).isEqualTo("no_active_device"));
    }

    @NotNull
    private WebTestClient.ResponseSpec sendChangeVolumeRequest(final int volume) {
        return webTestClient.put()
                .uri(b -> b.path("/player/volume")
                        .queryParam("volume_percent", volume)
                        .build())
                .header(AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }

    private void connectDevice() {
        final ConnectDeviceRequest connectDeviceRequest = ConnectDeviceRequestFaker.create().get();

        sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, connectDeviceRequest);
    }
}
