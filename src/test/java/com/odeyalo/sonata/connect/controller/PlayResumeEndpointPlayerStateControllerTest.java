package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.PlayerStateDtoAssert;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
@Disabled
public class PlayResumeEndpointPlayerStateControllerTest {
    @Autowired
    WebTestClient webTestClient;


    final String VALID_ACCESS_TOKEN = "Bearer mikunakanoisthebestgirl";
    final String VALID_USER_ID = "1";

    @BeforeAll
    void prepare() {
        Hooks.onOperatorDebug(); // DO NOT DELETE IT, VERY IMPORTANT LINE, WITHOUT IT FEIGN WITH WIREMOCK THROWS ILLEGAL STATE EXCEPTION, I DON'T FIND SOLUTION YET
    }

    // Endpoint requirements:
    /**
     * PUT
     * /player/play
     * response: 204 no content - command sent returns everytime, even if current player state is empty it should be just ignored
     * response: 401 - missing access token
     */

    @Test
    void shouldReturn204Status() {
        WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest();

        responseSpec.expectStatus().isNoContent();
    }

    @Test
    void shouldDoNothingIfPlayerWasJustCreated() {
        PlayerStateDto beforeRequest = getCurrentState();

        WebTestClient.ResponseSpec responseSpec = sendResumeEndpointRequest();

        PlayerStateDto afterRequest = getCurrentState();

        PlayerStateDtoAssert.forState(beforeRequest)
                .isEqualTo(afterRequest);
    }


    @NotNull
    private WebTestClient.ResponseSpec sendResumeEndpointRequest() {
        return webTestClient.put()
                .uri("/player/play")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange();
    }

    private PlayerStateDto getCurrentState() {
        return webTestClient.get()
                .uri("/player/state")
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                .exchange()
                .expectBody(PlayerStateDto.class)
                .returnResult()
                .getResponseBody();
    }
}
