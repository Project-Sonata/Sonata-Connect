package com.odeyalo.sonata.connect.controller;


import com.odeyalo.sonata.connect.dto.SonataConnectAuthenticationTokenResponseDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.SonataConnectAuthenticationTokenResponseDtoAssert;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
public class SonataConnectDeviceAuthenticationEndpointTest {

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
    class ValidRequestTests {

        @Test
        void shouldReturnOkStatus() {
            WebTestClient.ResponseSpec responseSpec = sendValidRequest();

            responseSpec.expectStatus().isOk();
        }

        @Test
        void shouldContainScatToken() {
            WebTestClient.ResponseSpec responseSpec = sendValidRequest();

            SonataConnectAuthenticationTokenResponseDto responseBody = responseSpec.expectBody(SonataConnectAuthenticationTokenResponseDto.class)
                    .returnResult().getResponseBody();

            SonataConnectAuthenticationTokenResponseDtoAssert.forResponseBody(responseBody)
                    .hasToken();
        }

        @NotNull
        private WebTestClient.ResponseSpec sendValidRequest() {
            return webTestClient.post()
                    .uri("/connect/auth")
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .exchange();
        }
    }
}
