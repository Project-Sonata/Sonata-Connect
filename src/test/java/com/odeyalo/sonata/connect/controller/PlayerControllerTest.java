package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.entity.InMemoryDevice;
import com.odeyalo.sonata.connect.entity.InMemoryDevices;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.DeviceType;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import testing.asserts.PlayerStateDtoAssert;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@TestPropertySource(locations = "classpath:application-test.properties")
class PlayerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerStateStorage playerStateStorage;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValidPlayerStateTests {

        @BeforeAll
        void prepareData() {
            InMemoryDevices devices = InMemoryDevices.builder()
                    .device(InMemoryDevice.builder()
                            .id("something")
                            .name("Miku")
                            .deviceType(DeviceType.COMPUTER)
                            .volume(50)
                            .active(true)
                            .build())
                    .build();
            PersistablePlayerState playerState = PersistablePlayerState.builder()
                    .id(1L)
                    .shuffleState(PlayerState.SHUFFLE_DISABLED)
                    .progressMs(0L)
                    .playing(true)
                    .playingType(PlayingType.TRACK)
                    .repeatState(RepeatState.OFF)
                    .devices(devices)
                    .build();
            playerStateStorage.save(playerState).block();
        }

        @Test
        void shouldReturn200() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            responseSpec.expectStatus().isOk();
        }

        @Test
        void shouldRespondWithApplicationJsonContentType() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            responseSpec.expectHeader().contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        void shouldReturnParsedCurrentStateInBody() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            responseSpec.expectBody(PlayerStateDto.class);
        }

        @Test
        void shouldIndicatePlayingState() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .shouldPlay();
        }

        @Test
        void shouldContainCurrentRepeatState() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .repeatState().off();
        }

        @Test
        void shouldContainCurrentShuffleState() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .shuffleState().off();
        }

        @Test
        void shouldContainCurrentlyPlayingType() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .currentlyPlayingType().track();
        }


        @Test
        void shouldContainProgressMs() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .progressMs(0L);
        }

        @Test
        void shouldContainDevicesListWithOneElement() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .devices().length(1);
        }

        @Test
        void shouldContainValidDeviceId() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .devices().peekFirst().id("something");
        }

        @Test
        void shouldContainValidDeviceName() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .devices().peekFirst().name("Miku");
        }

        @Test
        void shouldContainValidDeviceType() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .devices().peekFirst().type(DeviceType.COMPUTER);
        }

        @Test
        void shouldContainVolumePercentage() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .devices().peekFirst().volume(50);
        }

        @Test
        void deviceShouldBeActive() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .devices().peekFirst().active();
        }

        private WebTestClient.ResponseSpec sendCurrentPlayerStateRequest() {
            return webTestClient.get()
                    .uri("/player/state")
                    .exchange();
        }
    }
}