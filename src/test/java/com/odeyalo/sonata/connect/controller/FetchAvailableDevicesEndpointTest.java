package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.AvailableDevicesResponseDto;
import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
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
import testing.asserts.AvailableDevicesResponseDtoAssert;
import testing.faker.ConnectDeviceRequestFaker;
import testing.shared.SonataTestHttpOperations;
import testing.spring.autoconfigure.AutoConfigureSonataHttpClient;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureSonataHttpClient
public class FetchAvailableDevicesEndpointTest {

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
    class SingleAvailableDevicePlayerStateControllerTestEntity {
        DeviceEntity expectedDeviceEntity;

        @BeforeAll
        void beforeAll() {
            expectedDeviceEntity = connectSingleDevice();
        }

        @AfterAll
        void afterAll() {
            playerStateRepository.clear().block();
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
                    .id(expectedDeviceEntity.getId())
                    .name(expectedDeviceEntity.getName())
                    .volume(expectedDeviceEntity.getVolume())
                    .type(expectedDeviceEntity.getDeviceType());

        }

        private DeviceEntity connectSingleDevice() {
            ConnectDeviceRequest request = ConnectDeviceRequestFaker.create().get();
            sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, request);

            return DeviceEntity.builder()
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
