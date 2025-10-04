package com.odeyalo.sonata.connect.controller;


import com.odeyalo.sonata.connect.AbstractIntegrationTest;
import com.odeyalo.sonata.connect.dto.AvailableDevicesResponseDto;
import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.AvailableDevicesResponseDtoAssert;
import testing.faker.ConnectDeviceRequestFaker;
import testing.shared.SonataTestHttpOperations;

public final class DisconnectDeviceEndpointTest extends AbstractIntegrationTest {
    public static final String DISCONNECT_DEVICE_ENDPOINT = "/player/devices";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataTestHttpOperations sonataTestHttpOperations;

    @Autowired
    PlayerStateRepository playerStateRepository;

    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String DEVICE_ID_QUERY_PARAM_NAME = "device_id";

    @BeforeAll
    void setup() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    @Test
    void shouldReturnNoContentStatusOnRemovingExistingDevice() {
        ConnectDeviceRequest existingDevice = ConnectDeviceRequestFaker.create().get();
        sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, existingDevice);

        WebTestClient.ResponseSpec responseSpec = sendDisconnectDeviceRequest(existingDevice.getId());

        responseSpec.expectStatus().isNoContent();
    }

    @Test
    void shouldRemoveDeviceByItsId() {
        // given
        ConnectDeviceRequest existingDevice = ConnectDeviceRequestFaker.create().get();
        sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, existingDevice);
        // when
        sendDisconnectDeviceRequest(existingDevice.getId());
        // then
        AvailableDevicesResponseDto connectedDevices = sonataTestHttpOperations.getConnectedDevices(VALID_ACCESS_TOKEN);

        AvailableDevicesResponseDtoAssert.forBody(connectedDevices).isEmpty();
    }

    @Test
    void shouldReturnNoContentStatusOnRemovingNotExistingDevice() {
        WebTestClient.ResponseSpec responseSpec = sendDisconnectDeviceRequest("not_exist");

        responseSpec.expectStatus().isNoContent();
    }

    @NotNull
    private WebTestClient.ResponseSpec sendDisconnectDeviceRequest(@NotNull String deviceId) {
        return webTestClient.delete()
                .uri(builder -> builder.path(DISCONNECT_DEVICE_ENDPOINT)
                        .queryParam(DEVICE_ID_QUERY_PARAM_NAME, deviceId)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }
}
