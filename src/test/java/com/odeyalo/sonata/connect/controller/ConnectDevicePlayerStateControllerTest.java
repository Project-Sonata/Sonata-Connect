package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.ExceptionMessages;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
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
import testing.asserts.ExceptionMessagesAssert;
import testing.asserts.PlayerStateDtoAssert;
import testing.faker.ConnectDeviceRequestFaker;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class ConnectDevicePlayerStateControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PlayerStateStorage playerStateStorage;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @AfterEach
    void afterEach() {
        playerStateStorage.clear().block();
    }

    @Test
    void shouldReturnNoContentStatusCode() {
        WebTestClient.ResponseSpec exchange = prepareValidAndSend();

        exchange.expectStatus().isNoContent();
    }

    @Test
    void shouldReturnApplicationJson() {
        WebTestClient.ResponseSpec exchange = prepareValidAndSend();

        exchange.expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void deviceShouldBeAdded() {
        ConnectDeviceRequest body = ConnectDeviceRequestFaker.create().get();

        WebTestClient.ResponseSpec responseSpec = sendRequest(body);

        PlayerStateDto afterRequest = getCurrentPlayerState();

        PlayerStateDtoAssert.forState(afterRequest)
                .devices().length(1);
    }

    @Test
    void shouldUpdateStateWithValidDeviceInfo() {
        ConnectDeviceRequest body = ConnectDeviceRequestFaker.create().get();

        WebTestClient.ResponseSpec responseSpec = sendRequest(body);

        PlayerStateDto afterRequest = getCurrentPlayerState();

        PlayerStateDtoAssert.forState(afterRequest)
                .devices().peekFirst()
                .id(body.getId())
                .name(body.getName())
                .volume(body.getVolume())
                .type(body.getDeviceType());
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PlainJsonForIdFieldTests {

        @Test
        void sendWithNullIdValue_andExpect400StatusWithMessage() {
            String json = """
                    {
                        "id": null,
                        "name": "ilovemikunakano1",
                        "device_type": "COMPUTER",
                        "volume": 40
                    }""";
            WebTestClient.ResponseSpec responseSpec = sendRequestWithPlainJson(json);

            responseSpec.expectStatus().isBadRequest();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("Id of the device is required");
        }
        @Test
        void sendWithLessThanMinimumIdSize_andExpect400StatusWithMessage() {
            String json = """
                    {
                        "id": "id",
                        "name": "Odeyalooo",
                        "device_type": "COMPUTER",
                        "volume": 40
                    }""";
            WebTestClient.ResponseSpec responseSpec = sendRequestWithPlainJson(json);

            responseSpec.expectStatus().isBadRequest();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("Minimum size of ID for the device is 16 characters");
        }

        @Test
        void sendWithMoreThanMaximumIdSize_andExpect400StatusWithMessage() {
            String json = """
                    {
                        "id": "ilovemikunakanopleasebemydarling",
                        "name": "Odeyalooo",
                        "device_type": "COMPUTER",
                        "volume": 40
                    }""";
            WebTestClient.ResponseSpec responseSpec = sendRequestWithPlainJson(json);

            responseSpec.expectStatus().isBadRequest();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("Maximum size of ID for the device is 16 characters");
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PlainJsonForNameFieldTests {
        @Test
        void sendWithNullNameValue_andExpect400StatusWithMessage() {
            String json = """
                    {
                        "id": "ilovemikunakano1",
                        "name": null,
                        "device_type": "COMPUTER",
                        "volume": 40
                    }""";

            WebTestClient.ResponseSpec responseSpec = sendRequestWithPlainJson(json);

            responseSpec.expectStatus().isBadRequest();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("Name of the device is required");
        }

        @Test
        void sendWithLessThanMinimumNameSize_andExpect400StatusWithMessage() {
            String json = """
                    {
                        "id": "ilovemikunakano1",
                        "name": "ily",
                        "device_type": "COMPUTER",
                        "volume": 40
                    }""";
            WebTestClient.ResponseSpec responseSpec = sendRequestWithPlainJson(json);

            responseSpec.expectStatus().isBadRequest();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("Minimum size of name for the device is 4 characters");
        }

        @Test
        void sendWithMoreThanMaximumNameSize_andExpect400StatusWithMessage() {
            String json = """
                    {
                        "id": "ilovemikunakano1",
                        "name": "More than 16 characters",
                        "device_type": "COMPUTER",
                        "volume": 40
                    }""";
            WebTestClient.ResponseSpec responseSpec = sendRequestWithPlainJson(json);

            responseSpec.expectStatus().isBadRequest();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("Maximum size of name for the device is 16 characters");
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PlainJsonForDeviceTypeTests {

        @Test
        void sendWithInvalidDeviceTypeJsonKeyName_andExpect400Status() {
            WebTestClient.ResponseSpec responseSpec = sendRequestWithInvalidDeviceTypeJsonKey();

            responseSpec.expectStatus().isBadRequest();
        }

        @Test
        void sendWithInvalidDeviceTypeJsonKey_andExpectMessage() {
            WebTestClient.ResponseSpec responseSpec = sendRequestWithInvalidDeviceTypeJsonKey();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("'device_type' json field is missing or invalid");
        }

        @Test
        void sendWithInvalidDeviceTypeValue_andExpect400Status() {
            WebTestClient.ResponseSpec responseSpec = sendRequestWithInvalidDeviceTypeValue();

            responseSpec.expectStatus().isBadRequest();
        }

        @NotNull
        private WebTestClient.ResponseSpec sendRequestWithInvalidDeviceTypeJsonKey() {
            String json = """
                    {
                        "id": "ilovemikunakanoo",
                        "name": "Odeyalooo",
                        "type": "COMPUTER",
                        "volume": 40
                    }""";

            return sendRequestWithPlainJson(json);
        }

        @NotNull
        private WebTestClient.ResponseSpec sendRequestWithInvalidDeviceTypeValue() {
            String json = """
                    {
                        "id": "miiiku",
                        "name": "Odeyalooo",
                        "device_type": "Miku",
                        "volume": 40
                    }""";

            return sendRequestWithPlainJson(json);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PlainJsonForVolumeTests {

        @Test
        void sendRequestWithNegativeVolume() {
            String json = """
                    {
                        "id": "ilovemikunakanoo",
                        "name": "Odeyalooo",
                        "device_type": "COMPUTER",
                        "volume": -10
                    }""";

            WebTestClient.ResponseSpec responseSpec = sendRequestWithPlainJson(json);

            responseSpec.expectStatus().isBadRequest();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("Volume must be between 0-100");
        }

        @Test
        void sendRequestWithMoreThan100Volume() {
            String json = """
                    {
                        "id": "ilovemikunakanoo",
                        "name": "Odeyalooo",
                        "device_type": "COMPUTER",
                        "volume": 130
                    }""";

            WebTestClient.ResponseSpec responseSpec = sendRequestWithPlainJson(json);

            responseSpec.expectStatus().isBadRequest();

            ExceptionMessages messages = responseSpec.expectBody(ExceptionMessages.class).returnResult().getResponseBody();

            ExceptionMessagesAssert.forMessages(messages)
                    .length(1)
                    .peekFirst().isDescriptionEqualTo("Volume must be between 0-100");
        }
    }

    @NotNull
    private WebTestClient.ResponseSpec prepareValidAndSend() {
        ConnectDeviceRequest body = ConnectDeviceRequestFaker.create().get();
        return sendRequest(body);
    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequestWithPlainJson(String json) {
        return webTestClient.put()
                .uri("/player/device/connect")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange();
    }

    private PlayerStateDto getCurrentPlayerState() {
        return webTestClient.get()
                .uri("/player/state")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange().expectBody(PlayerStateDto.class)
                .returnResult().getResponseBody();

    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequest(ConnectDeviceRequest connectDeviceRequest) {
        return webTestClient.put()
                .uri("/player/device/connect")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(connectDeviceRequest)
                .exchange();
    }
}
