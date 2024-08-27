package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.*;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.AvailableDevicesResponseDtoAssert;
import testing.asserts.ExceptionMessageAssert;
import testing.asserts.ReasonCodeAwareExceptionMessageAssert;
import testing.faker.ConnectDeviceRequestFaker;
import testing.shared.SonataTestHttpOperations;
import testing.spring.autoconfigure.AutoConfigureSonataHttpClient;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataHttpClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@ActiveProfiles("test")
public class SwitchDevicesEndpointTest {
    public static final String DEVICE_SWITCH_ENDPOINT = "/player/devices";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataTestHttpOperations sonataTestHttpOperations;

    @Autowired
    PlayerStateRepository playerStateRepository;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EmptyDeviceListRequestTestsEntity {

        @Test
        void shouldReturnBadRequest() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();
            responseSpec.expectStatus().isBadRequest();
        }

        @Test
        void shouldReturnExceptionReason() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

            ReasonCodeAwareExceptionMessageAssert.forMessage(body).reasonCode().isEqualTo("target_device_required");
        }

        @Test
        void shouldReturnMessage() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            ReasonCodeAwareExceptionMessage body = responseSpec.expectBody(ReasonCodeAwareExceptionMessage.class).returnResult().getResponseBody();

            ReasonCodeAwareExceptionMessageAssert.forMessage(body).description().isEqualTo("Target device is required!");
        }

        @NotNull
        private WebTestClient.ResponseSpec prepareAndSend() {
            DeviceSwitchRequest body = DeviceSwitchRequest.of(new String[0]);

            return sendRequest(body, VALID_ACCESS_TOKEN);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NotExistingDeviceRegistryTestsEntity {

        @Test
        void shouldReturnUnprocessableEntityStatus() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
        }

        @Test
        void shouldReturnApplicationJson() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectHeader().contentType(APPLICATION_JSON);
        }

        @Test
        void shouldReturnExceptionMessage() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            ExceptionMessage message = responseSpec.expectBody(ExceptionMessage.class).returnResult().getResponseBody();

            ExceptionMessageAssert.forMessage(message).isDescriptionEqualTo("Device with ID: not_existing not found!");
        }

        @NotNull
        private WebTestClient.ResponseSpec prepareAndSend() {
            DeviceSwitchRequest body = DeviceSwitchRequest.of(new String[]{"not_existing"});

            return sendRequest(body, VALID_ACCESS_TOKEN);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SingleDeviceSwitchTestsEntity {
        String activeDeviceId;
        String inactiveDeviceId;

        @BeforeEach
        void prepare() {
            ConnectDeviceRequest firstDevice = ConnectDeviceRequestFaker.create().get();
            ConnectDeviceRequest secondDevice = ConnectDeviceRequestFaker.create().get();

            sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, firstDevice);
            sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, secondDevice);

            activeDeviceId = firstDevice.getId();
            inactiveDeviceId = secondDevice.getId();
        }

        @AfterEach
        void clean() {
            playerStateRepository.clear().block();
        }

        @Test
        void shouldReturnNoContentOnSuccess() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            responseSpec.expectStatus().isNoContent();
        }

        @Test
        void shouldMakeDeviceInactiveToPreviouslyActiveDevice() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            AvailableDevicesResponseDto devices = sonataTestHttpOperations.getConnectedDevices(VALID_ACCESS_TOKEN);

            AvailableDevicesResponseDtoAssert.forBody(devices)
                    .devices().peekById(activeDeviceId).inactive();
        }

        @Test
        void shouldMakeDeviceActive() {
            WebTestClient.ResponseSpec responseSpec = prepareAndSend();

            AvailableDevicesResponseDto devices = sonataTestHttpOperations.getConnectedDevices(VALID_ACCESS_TOKEN);

            AvailableDevicesResponseDtoAssert.forBody(devices)
                            .devices().peekById(inactiveDeviceId).active(); // Check if the inactive device became active
        }

        @Test
        void shouldReturn422StatusCodeIfDeviceIdIsInvalid() {
            DeviceSwitchRequest body = DeviceSwitchRequest.of(new String[]{"not_existing"});

            WebTestClient.ResponseSpec responseSpec = sendRequest(body, VALID_ACCESS_TOKEN);

            responseSpec.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
        }

        @NotNull
        private WebTestClient.ResponseSpec prepareAndSend() {
            DeviceSwitchRequest body = DeviceSwitchRequest.of(new String[]{inactiveDeviceId});

            return sendRequest(body, VALID_ACCESS_TOKEN);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MultipleDeviceIdsInBodyTestsEntity {

        @Test
        void shouldReturn400BadRequestStatusCode() {
            DeviceSwitchRequest body = DeviceSwitchRequest.of(
                    new String[]{"something", "ilovemiku", "thirddeviceid"});
            WebTestClient.ResponseSpec responseSpec = sendRequest(body, VALID_ACCESS_TOKEN);

            responseSpec.expectStatus().isBadRequest();
        }

        @Test
        void shouldContainMessage() {
            DeviceSwitchRequest body = DeviceSwitchRequest.of(
                    new String[]{"something", "ilovemiku", "thirddeviceid"});
            WebTestClient.ResponseSpec responseSpec = sendRequest(body, VALID_ACCESS_TOKEN);

            ExceptionMessage message = responseSpec.expectBody(ExceptionMessage.class).returnResult().getResponseBody();

            ExceptionMessageAssert.forMessage(message)
                    .isDescriptionEqualTo("One and only one deviceId should be provided. More than one is not supported now");
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UnauthorizedRequestTest {
        String INVALID_ACCESS_TOKEN = "invalidtoken";
        @Test
        void shouldReturn401() {
            WebTestClient.ResponseSpec responseSpec = prepareInvalidAndSend();
            responseSpec.expectStatus().isUnauthorized();
        }

        @Test
        void shouldReturnApplicationJson() {
            WebTestClient.ResponseSpec responseSpec = prepareInvalidAndSend();

            responseSpec.expectHeader().contentType(APPLICATION_JSON);
        }

        @Test
        void shouldReturnExceptionMessage() {
            WebTestClient.ResponseSpec responseSpec = prepareInvalidAndSend();

            ExceptionMessage body = responseSpec.expectBody(ExceptionMessage.class)
                    .returnResult().getResponseBody();

            ExceptionMessageAssert.forMessage(body)
                    .isDescriptionEqualTo("Missing access token or token has been expired");
        }

        @NotNull
        private WebTestClient.ResponseSpec prepareInvalidAndSend() {
            DeviceSwitchRequest request = DeviceSwitchRequest.of(new String[]{"something"});

            return sendRequest(request, INVALID_ACCESS_TOKEN);
        }
    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequest(DeviceSwitchRequest requestBody, @SuppressWarnings("SameParameterValue") String authorizationHeaderValue) {
        WebTestClient.RequestBodySpec builder = webTestClient.put()
                .uri(DEVICE_SWITCH_ENDPOINT);

        if (requestBody != null) {
            builder.bodyValue(requestBody);
        }
        builder.header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);

        if (authorizationHeaderValue != null) {
            builder.header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue);
        }

        return builder.exchange();
    }
}
