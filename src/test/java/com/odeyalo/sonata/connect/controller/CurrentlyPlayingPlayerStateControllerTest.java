package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.CurrentlyPlayingPlayerStateDto;
import com.odeyalo.sonata.connect.entity.InMemoryUserEntity;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
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
    PlayerStateStorage playerStateStorage;

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
        PersistablePlayerState expectedState;

        @BeforeEach
        void beforeEach() {
            PersistablePlayerState playerState = createPlayingState();

            expectedState = playerStateStorage.save(playerState).block();
        }

        @AfterEach
        void afterEach() {
            playerStateStorage.clear().block();
        }

        @Test
        void shouldReturnCurrentlyPlayingState() {
            CurrentlyPlayingPlayerStateDto body = sendAndGetBody();

            CurrentlyPlayingPlayerStateDtoAssert.forBody(body)
                    .shuffleState().isEqualTo(expectedState.getShuffleState());
        }

        private CurrentlyPlayingPlayerStateDto sendAndGetBody() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();
            return responseSpec.expectBody(CurrentlyPlayingPlayerStateDto.class).returnResult().getResponseBody();
        }

        @Test
        void shouldReturnApplicationJsonContentType() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            responseSpec.expectHeader().contentType(MediaType.APPLICATION_JSON);
        }

        private PersistablePlayerState createPlayingState() {
            return PlayerStateFaker.create()
                    .setPlaying(true)
                    .setUser(new InMemoryUserEntity(VALID_USER_ID))
                    .asPersistablePlayerState();
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
    class CurrentlyPlayingForUnathorizedRequest {

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