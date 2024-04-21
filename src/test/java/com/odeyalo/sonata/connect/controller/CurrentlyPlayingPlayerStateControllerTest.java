package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.CurrentlyPlayingPlayerStateDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.support.mapper.DevicesEntity2DevicesConverter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.Devices2DevicesDtoConverter;
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
import testing.asserts.CurrentlyPlayingPlayerStateDtoAssert;
import testing.faker.PlayerStateFaker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class CurrentlyPlayingPlayerStateControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerStateRepository playerStateRepository;

    @Autowired
    DevicesEntity2DevicesConverter devicesEntity2DevicesConverter;
    @Autowired
    Devices2DevicesDtoConverter devicesDtoConverter;

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
        void shouldReturnAvailableDevices() {
            DevicesEntity expectedDevicesEntity = expectedState.getDevicesEntity();

            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            CurrentlyPlayingPlayerStateDtoAssert.forBody(body)
                    .devices().length(expectedDevicesEntity.size());

            Devices model = devicesEntity2DevicesConverter.convertTo(expectedDevicesEntity);

            DevicesDto dto = devicesDtoConverter.convertTo(model);

            DevicesDto devices = body.getDevices();
            assertThat(devices.getDevices()).containsAll(dto.getDevices());
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