package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.AbstractIntegrationTest;
import com.odeyalo.sonata.connect.dto.CurrentlyPlayingPlayerStateDto;
import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.CurrentlyPlayingPlayerStateDtoAssert;
import testing.faker.PlayerStateFaker;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrentlyPlayingEndpointTest extends AbstractIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerStateRepository playerStateRepository;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";
    final String INVALID_ACCESS_TOKEN = "Bearer invalidtoken";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CurrentlyPlayingForValidUser {
        PlayerStateEntity expectedState;

        @BeforeEach
        void beforeEach() {
            PlayerStateEntity playerState = createPlayingState();

            expectedState = playerStateRepository.save(playerState).block();
        }

        @AfterEach
        void afterEach() {
            playerStateRepository.clear().block();
        }

        @Test
        void shouldReturn200StatusCode() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            responseSpec.expectStatus().isOk();
        }

        @Test
        void shouldReturnApplicationJsonContentType() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            responseSpec.expectHeader().contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        void shouldReturnCurrentlyShuffleState() {
            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            CurrentlyPlayingPlayerStateDtoAssert.forBody(body)
                    .shuffleState().isEqualTo(expectedState.getShuffleState());
        }

        @Test
        void shouldReturnRepeatState() {
            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();
            CurrentlyPlayingPlayerStateDtoAssert.forBody(body)
                    .repeatState().isEqualTo(expectedState.getRepeatState());
        }

        @Test
        void shouldReturnTrueInIsPlayingField() {
            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            CurrentlyPlayingPlayerStateDtoAssert.forBody(body)
                    .shouldPlay();
        }

        @Test
        void shouldReturnCurrentlyPlayingType() {
            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            CurrentlyPlayingPlayerStateDtoAssert.forBody(body)
                    .currentlyPlayingType().isEqualTo(expectedState.getPlayingType());
        }

        @Test
        void shouldReturnPlayingItem() {
            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            PlayableItemEntity expectedItem = expectedState.getCurrentlyPlayingItem();

            CurrentlyPlayingPlayerStateDtoAssert.forBody(body)
                    .currentlyPlayingItem().id().isEqualTo(expectedItem.getId());


            CurrentlyPlayingPlayerStateDtoAssert.forBody(body)
                    .currentlyPlayingItem().itemType().isEqualTo(expectedItem.getType());
        }

        @Test
        void shouldReturnAvailableDeviceIds() {
            List<String> expectedDeviceIds = getExpectedDeviceX(expectedState, DeviceEntity::getId);

            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            List<String> actualDeviceIds = getActualDeviceX(body, DeviceDto::getDeviceId);

            assertThat(actualDeviceIds).containsAll(expectedDeviceIds);
        }

        @Test
        void shouldReturnAvailableDeviceNames() {
            List<String> expectedDeviceNames = getExpectedDeviceX(expectedState, DeviceEntity::getName);

            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            List<String> actualDeviceNames = getActualDeviceX(body, DeviceDto::getDeviceName);

            assertThat(actualDeviceNames).containsAll(expectedDeviceNames);
        }

        @Test
        void shouldReturnAvailableDeviceTypes() {
            DevicesEntity devices = expectedState.getDevices();

            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            assertDevicesHaveCorrectDeviceType(devices, body);
        }

        @Test
        void shouldReturnAvailableDeviceVolumes() {
            DevicesEntity devices = expectedState.getDevices();

            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            assertDevicesHaveCorrectDeviceVolume(devices, body);
        }

        private void assertDevicesHaveCorrectDeviceType(DevicesEntity devices, CurrentlyPlayingPlayerStateDto body) {
            assertDeviceHaveCorrectX(devices, body,
                    (deviceEntity, deviceDto) -> assertThat(deviceEntity.getDeviceType()).isEqualTo(deviceDto.getDeviceType())
            );
        }

        private void assertDevicesHaveCorrectDeviceVolume(DevicesEntity devices, CurrentlyPlayingPlayerStateDto body) {
            assertDeviceHaveCorrectX(devices, body,
                    (deviceEntity, deviceDto) -> assertThat(deviceEntity.getVolume()).isEqualTo(deviceDto.getVolume())
            );
        }

        private void assertDeviceHaveCorrectX(DevicesEntity devices, CurrentlyPlayingPlayerStateDto body, BiConsumer<DeviceEntity, DeviceDto> requirement) {
            assertThat(body.getDevices().getDevices())
                    .allSatisfy(deviceDto -> {
                        Optional<DeviceEntity> found = devices.findById(deviceDto.getDeviceId());
                        assertThat(found).isPresent();

                        requirement.accept(found.get(), deviceDto);
                    });
        }

        @NotNull
        private <T> List<T> getExpectedDeviceX(PlayerStateEntity playerState, Function<DeviceEntity, T> mapper) {
            return playerState.getDevices()
                    .stream()
                    .map(mapper)
                    .toList();
        }

        @NotNull
        private <T> List<T> getActualDeviceX(CurrentlyPlayingPlayerStateDto body, Function<DeviceDto, T> mapper) {
            return body.getDevices()
                    .getDevices()
                    .stream()
                    .map(mapper)
                    .toList();
        }

        private CurrentlyPlayingPlayerStateDto sendAndGetBody() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();
            return responseSpec.expectBody(CurrentlyPlayingPlayerStateDto.class).returnResult().getResponseBody();
        }

        private PlayerStateEntity createPlayingState() {
            return PlayerStateFaker.active()
                    .user(new UserEntity(VALID_USER_ID))
                    .get();
        }

        public WebTestClient.ResponseSpec sendRequest() {
            return webTestClient.get()
                    .uri("/player/currently-playing")
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .exchange();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CurrentlyPlayingForNothingPlayingWithValidRequestTests {

        @Test
        void shouldReturnNoContentStatus() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            responseSpec.expectStatus().isNoContent();
        }

        public WebTestClient.ResponseSpec sendRequest() {
            return webTestClient.get()
                    .uri("/player/currently-playing")
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .exchange();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CurrentlyPlayingForUnauthorizedRequest {

        @Test
        void shouldReturn401Status() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            responseSpec.expectStatus().isUnauthorized();
        }

        @Test
        void shouldReturnApplicationJson() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            responseSpec.expectHeader().contentType(MediaType.APPLICATION_JSON);
        }

        public WebTestClient.ResponseSpec sendRequest() {
            return webTestClient.get()
                    .uri("/player/currently-playing")
                    .header(HttpHeaders.AUTHORIZATION, INVALID_ACCESS_TOKEN)
                    .exchange();
        }
    }
}