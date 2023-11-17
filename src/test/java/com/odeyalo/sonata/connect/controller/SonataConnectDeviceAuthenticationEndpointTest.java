package com.odeyalo.sonata.connect.controller;


import com.odeyalo.sonata.connect.controller.SonataConnectDeviceAuthenticationEndpointTest.Configuration;
import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.dto.SonataConnectAuthenticationTokenResponseDto;
import com.odeyalo.sonata.connect.service.connect.MockSonataConnectAccessTokenGenerator;
import com.odeyalo.sonata.connect.service.connect.SonataConnectAccessTokenGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Hooks;
import testing.asserts.SonataConnectAuthenticationTokenResponseDtoAssert;
import testing.faker.ConnectDeviceRequestFaker;
import testing.shared.SonataTestHttpOperations;
import testing.spring.autoconfigure.AutoConfigureSonataHttpClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataHttpClient
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "git://https://github.com/Project-Sonata/Sonata-Contracts.git",
        ids = "com.odeyalo.sonata:authorization:+")
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(Configuration.class)
public class SonataConnectDeviceAuthenticationEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    SonataTestHttpOperations sonataTestHttpOperations;

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
            System.out.println(responseBody);
            SonataConnectAuthenticationTokenResponseDtoAssert.forResponseBody(responseBody)
                    .hasToken();
        }

        @Test
        void scatCanBeExchangedForAccessToken() {
            WebTestClient.ResponseSpec responseSpec = sendValidRequest();

            SonataConnectAuthenticationTokenResponseDto responseBody = responseSpec.expectBody(SonataConnectAuthenticationTokenResponseDto.class)
                    .returnResult().getResponseBody();

            assertThat(responseBody).isNotNull();

            // HTTP status already asserted in exchangeScat method, won't do it again
            sonataTestHttpOperations.exchangeScat(VALID_ACCESS_TOKEN, responseBody.getToken());
        }

        @NotNull
        private WebTestClient.ResponseSpec sendValidRequest() {

            ConnectDeviceRequest existingDevice = ConnectDeviceRequestFaker.create().get();
            sonataTestHttpOperations.connectDevice(VALID_ACCESS_TOKEN, existingDevice);

            return webTestClient.post()
                    .uri((builder) -> builder.path("/connect/auth")
                            .queryParam("target_device_id", existingDevice.getId())
                            .build())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .exchange();
        }
    }

    @TestConfiguration
    static class Configuration {

        @Bean
        public SonataConnectAccessTokenGenerator sonataConnectAccessTokenGenerator() {
            return new MockSonataConnectAccessTokenGenerator();
        }
    }
}
