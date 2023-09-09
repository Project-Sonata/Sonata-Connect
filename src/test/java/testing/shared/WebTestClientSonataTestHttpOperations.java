package testing.shared;

import com.odeyalo.sonata.connect.dto.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Call endpoints using WebTestClient
 */
public class WebTestClientSonataTestHttpOperations implements SonataTestHttpOperations {
    private final WebTestClient webTestClient;

    public WebTestClientSonataTestHttpOperations(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Override
    public PlayerStateDto getCurrentState(String authorizationHeaderValue) {
        return webTestClient.get()
                .uri("/player/currently-playing")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                .exchange().expectBody(PlayerStateDto.class)
                .returnResult().getResponseBody();
    }

    @Override
    public AvailableDevicesResponseDto getConnectedDevices(String authorizationHeaderValue) {
        return webTestClient.get()
                .uri("/player/devices")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                .exchange().expectBody(AvailableDevicesResponseDto.class)
                .returnResult().getResponseBody();
    }

    @Override
    public void connectDevice(String authorizationHeaderValue, ConnectDeviceRequest body) {
        webTestClient.put()
                .uri("/player/device/connect")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    @Override
    public void playOrResumePlayback(String authorizationHeaderValue, PlayResumePlaybackRequest body) {
        webTestClient.put()
                .uri("/player/play")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    @Override
    public void changeShuffle(String authorizationHeaderValue, boolean shuffleMode) {
        webTestClient.put()
                .uri(builder -> {
                    builder.queryParam("state", shuffleMode);
                    return builder.path("/player/shuffle").build();
                })
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                .exchange();
    }

    @Override
    public void switchDevices(String authorizationHeaderValue, DeviceSwitchRequest body) {
        webTestClient.put()
                .uri("/player/device/switch")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                .bodyValue(body);
    }
}