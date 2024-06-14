package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.model.DeviceType;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
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
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayerStateFaker;
import testing.faker.UserEntityFaker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
class CurrentPlayerStatePlayerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerStateRepository playerStateRepository;

    @BeforeAll
    void prepare() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValidPlayerStateTests {
        final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
        final String VALID_USER_ID = "1";

        @BeforeAll
        void prepareData() {
            DevicesEntity devices = DevicesEntity.builder()
                    .item(DeviceEntityFaker.create()
                            .setDeviceId("something")
                            .setDeviceName("Miku")
                            .setDeviceType(DeviceType.COMPUTER)
                            .setVolume(50)
                            .setActive(true)
                            .asInMemoryDevice())
                    .build();
            UserEntity user = UserEntityFaker.create().setId(VALID_USER_ID).get();

            PlayerStateEntity playerState = PlayerStateFaker.createWithCustomNumberOfDevices(1)
                    .id(1L)
                    .shuffleState(PlayerStateEntity.SHUFFLE_DISABLED)
                    .progressMs(0L)
                    .playing(true)
                    .playingType(PlayingType.TRACK)
                    .repeatState(RepeatState.OFF)
                    .devicesEntity(devices)
                    .user(user)
                    .currentlyPlayingItem(TrackItemEntity.of("mikuyouaremyqueen", "my_track_name"))
                    .get();
            playerStateRepository.save(playerState).block();
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
        @Disabled("Test class must be rewritten to black box. Wrong progress is returned")
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

        @Test
        void currentPlayingTrackMustBeReturned() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .track().id().isEqualTo("mikuyouaremyqueen");
        }

        @Test
        void shouldReturnCurrentTrackName() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .track().hasName("my_track_name");
        }

        private WebTestClient.ResponseSpec sendCurrentPlayerStateRequest() {
            return webTestClient.get()
                    .uri("/player/state")
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .exchange();
        }
    }

    @Nested
    class UnauthorizedRequestTests {
        final String INVALID_ACCESS_TOKEN = "Bearer invalidtoken";

        @Test
        void expect401() {
            WebTestClient.ResponseSpec responseSpec = sendUnauthorizedRequest();

            responseSpec.expectStatus().isUnauthorized();
        }

        @Test
        void expectApplicationJson() {
            WebTestClient.ResponseSpec responseSpec = sendUnauthorizedRequest();

            responseSpec.expectHeader().contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        void expectNotNullBody() {
            WebTestClient.ResponseSpec responseSpec = sendUnauthorizedRequest();

            ExceptionMessage message = responseSpec.expectBody(ExceptionMessage.class).returnResult().getResponseBody();

            assertThat(message)
                    .as("Body must be not null")
                    .isNotNull();
        }

        @Test
        void expectMessageInBody() {
            WebTestClient.ResponseSpec responseSpec = sendUnauthorizedRequest();

            ExceptionMessage message = responseSpec.expectBody(ExceptionMessage.class).returnResult().getResponseBody();

            assertThat(message.getDescription())
                    .as("Body must contain message with description")
                    .isEqualTo("Missing access token or token has been expired");
        }

        private WebTestClient.ResponseSpec sendUnauthorizedRequest() {
            return webTestClient.get()
                    .uri("/player/state")
                    .header(HttpHeaders.AUTHORIZATION, INVALID_ACCESS_TOKEN)
                    .exchange();
        }
    }

    @AfterAll
    void afterAll() {
        playerStateRepository.clear().block();
    }
}