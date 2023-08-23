package com.odeyalo.sonata.connect.controller;


import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.entity.InMemoryDevice;
import com.odeyalo.sonata.connect.entity.InMemoryDevices;
import com.odeyalo.sonata.connect.model.DeviceType;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
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
import testing.faker.DeviceFaker;
import testing.faker.DevicesFaker;
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
public class UpdatePlayerStatePlayerControllerTest {

    @Autowired
    WebTestClient testClient;

    @Autowired
    PlayerStateStorage playerStateStorage;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void prepare() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
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
            return testClient.get()
                    .uri("/player/shuffle")
                    .header(HttpHeaders.AUTHORIZATION, INVALID_ACCESS_TOKEN)
                    .exchange();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ChangeShuffleModeTests {

        @BeforeAll
        void prepareData() {
            InMemoryDevices devices = InMemoryDevices.builder()
                    .device(DeviceFaker.create()
                            .setDeviceId("something")
                            .setDeviceName("Miku")
                            .setDeviceType(DeviceType.COMPUTER)
                            .setVolume(50)
                            .setActive(true)
                            .asInMemoryDevice())
                    .build();
            PersistablePlayerState playerState = PlayerStateFaker.createWithCustomNumberOfDevices(1)
                    .setDevices(devices)
                    .asPersistablePlayerState();
            playerStateStorage.save(playerState).block();
        }

        @Test
        void expect204Status() {
            WebTestClient.ResponseSpec responseSpec = sendValidChangeShuffleMode(true);

            responseSpec.expectStatus().isNoContent();
        }

        @Test
        void expectStateToBeChanged() {
            PlayerStateDto currentState = getCurrentState();

            boolean negatedShuffleState = negateShuffleState(currentState);

            WebTestClient.ResponseSpec responseSpec = sendValidChangeShuffleMode(negatedShuffleState);

            PlayerStateDto updatedState = getCurrentState();

            assertThat(currentState)
                    .as("The state must be updated!")
                    .isNotEqualTo(updatedState);

            assertThat(updatedState.getShuffleState())
                    .as("The state must be negated!")
                    .isEqualTo(negatedShuffleState);

        }

        private boolean negateShuffleState(PlayerStateDto currentState) {
            return !currentState.getShuffleState();
        }

        private PlayerStateDto getCurrentState() {
            return testClient.get()
                    .uri("/player/state")
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .exchange()
                    .expectBody(PlayerStateDto.class)
                    .returnResult()
                    .getResponseBody();
        }

        @Test
        void sendRequestWithoutParams_andExpect400() {
            WebTestClient.ResponseSpec responseSpec = sendValidChangeShuffleMode(null);

            responseSpec.expectStatus().isBadRequest();
        }

        @NotNull
        private WebTestClient.ResponseSpec sendValidChangeShuffleMode(Boolean state) {
            return testClient.put()
                    .uri(builder -> {
                        if (state != null) {
                            builder.queryParam("state", state);
                        }
                        return builder.path("/player/shuffle").build();
                    })
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .exchange();
        }
    }

    @AfterAll
    void afterAll() {
        playerStateStorage.clear().block();
    }
}
