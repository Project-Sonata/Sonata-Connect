package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.model.DeviceType;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.TrackItemSpec;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
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

import java.net.URI;

import static com.odeyalo.sonata.connect.model.PlayableItemDuration.ofMilliseconds;
import static com.odeyalo.sonata.connect.model.ShuffleMode.OFF;
import static com.odeyalo.sonata.connect.model.track.AlbumSpec.AlbumType.SINGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
class CurrentPlayerStateEndpointTest {

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

            ArtistListEntity artists = ArtistListEntity.solo(
                    ArtistEntity.of(
                            ArtistSpec.ArtistId.of("123"),
                            "BONES",
                            ContextUri.forArtist("123")
                    )
            );
            PlayerStateEntity playerState = PlayerStateFaker.createWithCustomNumberOfDevices(1)
                    .id(1L)
                    .shuffleState(OFF)
                    .progressMs(0L)
                    .playing(true)
                    .playingType(PlayingType.TRACK)
                    .repeatState(RepeatState.OFF)
                    .devicesEntity(devices)
                    .user(user)
                    .currentlyPlayingItem(
                            TrackItemEntity.builder()
                                    .id("mikuyouaremyqueen")
                                    .name("my_track_name")
                                    .duration(ofMilliseconds(148_000L))
                                    .contextUri(ContextUri.forTrack("mikuyouaremyqueen"))
                                    .explicit(true)
                                    .order(TrackItemSpec.Order.of(1, 3))
                                    .artists(artists)
                                    .album(
                                            AlbumEntity.builder()
                                                    .id(AlbumSpec.AlbumId.of("miku"))
                                                    .name("melanchole")
                                                    .albumType(SINGLE)
                                                    .totalTrackCount(2)
                                                    .artists(artists)
                                                    .images(
                                                            ImageListEntity.single(
                                                                    ImageEntity.builder()
                                                                            .url(URI.create("http://localhost:3000/image/123"))
                                                                            .height(300)
                                                                            .width(300)
                                                                            .build()
                                                            ))
                                                    .build()
                                    )
                                    .build())
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
        void shouldContainProgressMs() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            responseSpec.expectBody(PlayerStateDto.class)
                    .value(body -> assertThat(body.getProgressMs()).isGreaterThanOrEqualTo(0L));
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

        @Test
        void shouldReturnCurrentTrackDurationMs() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .track().hasDurationMs(148_000L);
        }

        @Test
        void shouldReturnCurrentTrackContextUri() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .track().hasContextUri("sonata:track:mikuyouaremyqueen");
        }

        @Test
        void shouldReturnCurrentTrackExplicitState() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .track().isExplicit(true);
        }

        @Test
        void shouldReturnCurrentTrackDiscNumber() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .track().hasDiscNumber(1);
        }

        @Test
        void shouldReturnCurrentTrack() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .track().hasNumber(3);
        }

        @Test
        void shouldReturnCurrentTrackArtists() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .artists().hasSize(1);
        }

        @Test
        void shouldReturnCurrentTrackArtistName() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .artists().peekFirst()
                    .hasName("BONES");
        }

        @Test
        void shouldReturnCurrentTrackArtistId() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .artists().peekFirst()
                    .hasId("123");
        }

        @Test
        void shouldReturnCurrentTrackArtistContextUri() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .artists().peekFirst()
                    .hasContextUri("sonata:artist:123");
        }

        @Test
        void shouldReturnCurrentTrackAlbumId() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album().hasId("miku");
        }

        @Test
        void shouldReturnCurrentTrackAlbumName() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album().hasName("melanchole");
        }

        @Test
        void shouldReturnCurrentTrackAlbumContextUri() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album().hasContextUri("sonata:album:miku");
        }

        @Test
        void shouldReturnCurrentTrackAlbumType() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album().hasAlbumType(SINGLE);
        }

        @Test
        void shouldReturnCurrentTrackAlbumTotalTracksCount() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album().hasTotalTrackCount(2);
        }

        @Test
        void shouldReturnCurrentTrackAlbumArtists() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album().artists().hasSize(1);
        }

        @Test
        void shouldReturnCurrentTrackAlbumArtistName() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album()
                    .artists().peekFirst()
                    .hasName("BONES");
        }

        @Test
        void shouldReturnCurrentTrackAlbumArtistId() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album()
                    .artists().peekFirst()
                    .hasId("123");
        }

        @Test
        void shouldReturnCurrentTrackAlbumArtistContextUri() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album()
                    .artists().peekFirst()
                    .hasContextUri("sonata:artist:123");
        }

        @Test
        void shouldReturnAlbumImages() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album()
                    .images().hasSize(1);
        }

        @Test
        void shouldReturnAlbumImageUri() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album()
                    .images().first()
                    .hasUri("http://localhost:3000/image/123");
        }

        @Test
        void shouldReturnAlbumImageWidth() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album()
                    .images().first()
                    .hasWidth(300);
        }

        @Test
        void shouldReturnAlbumImageHeight() {
            WebTestClient.ResponseSpec responseSpec = sendCurrentPlayerStateRequest();

            PlayerStateDto body = responseSpec.expectBody(PlayerStateDto.class).returnResult().getResponseBody();

            PlayerStateDtoAssert.forState(body)
                    .album()
                    .images().first()
                    .hasHeight(300);
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

            assertThat(message).isNotNull();
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