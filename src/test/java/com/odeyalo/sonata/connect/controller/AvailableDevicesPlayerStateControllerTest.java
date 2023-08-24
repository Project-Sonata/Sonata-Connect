package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.AvailableDevicesResponseDto;
import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.InMemoryDevice;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.AvailableDevicesResponseDtoAssert;
import testing.faker.ConnectDeviceRequestFaker;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class AvailableDevicesPlayerStateControllerTest {

    @Autowired
    WebTestClient webTestClient;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SingleAvailableDevicePlayerStateControllerTest {
        Device expectedDevice;

        @BeforeAll
        void beforeAll() {
            expectedDevice =connectSingleDevice();
        }

        @NotNull
        private WebTestClient.ResponseSpec sendConnectDeviceRequest(ConnectDeviceRequest connectDeviceRequest) {
            return webTestClient.put()
                    .uri("/player/device/connect")
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(connectDeviceRequest)
                    .exchange();
        }

        @Test
        void shouldReturn200Status() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            responseSpec.expectStatus().isOk();
        }
        @Test
        void shouldReturnApplicationJson() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            responseSpec.expectHeader().contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        void shouldReturnAvailableDevicesBody() {
            WebTestClient.ResponseSpec responseSpec = sendRequest();

            AvailableDevicesResponseDto body = responseSpec.expectBody(AvailableDevicesResponseDto.class)
                    .returnResult().getResponseBody();

            AvailableDevicesResponseDtoAssert.forBody(body)
                    .devices().length(1)
                    .peekFirst()
                    .id(expectedDevice.getId())
                    .name(expectedDevice.getName())
                    .volume(expectedDevice.getVolume())
                    .type(expectedDevice.getDeviceType());

        }

        private Device connectSingleDevice() {
            ConnectDeviceRequest request = ConnectDeviceRequestFaker.create().get();
            sendConnectDeviceRequest(request);
            return InMemoryDevice.builder()
                    .id(request.getId())
                    .name(request.getName())
                    .active(true)
                    .volume(request.getVolume())
                    .deviceType(request.getDeviceType())
                    .build();
        }
    }

    @NotNull
    private WebTestClient.ResponseSpec sendRequest() {
        return webTestClient.get()
                .uri("/player/devices")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}
