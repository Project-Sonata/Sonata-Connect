package com.odeyalo.sonata.connect.controller;


import com.odeyalo.sonata.connect.AbstractIntegrationTest;
import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.DeviceType;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayerStateFaker;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeShuffleStateEndpointTest extends AbstractIntegrationTest {

    @Autowired
    WebTestClient testClient;

    @Autowired
    PlayerStateRepository playerStateRepository;

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
            DevicesEntity devices = DevicesEntity.builder()
                    .item(DeviceEntityFaker.create()
                            .setDeviceId("something")
                            .setDeviceName("Miku")
                            .setDeviceType(DeviceType.COMPUTER)
                            .setVolume(50)
                            .setActive(true)
                            .asInMemoryDevice())
                    .build();
            PlayerStateEntity playerState = PlayerStateFaker.createWithCustomNumberOfDevices(1)
                    .devicesEntity(devices)
                    .get();
            playerStateRepository.save(playerState).block();
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
        playerStateRepository.clear().block();
    }
}
