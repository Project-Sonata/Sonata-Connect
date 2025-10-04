package com.odeyalo.sonata.connect;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import testing.spring.autoconfigure.AutoConfigureSonataHttpClient;
import testing.spring.autoconfigure.AutoConfigureWebSocketClient;
import testing.spring.stubs.AutoConfigureSonataStubs;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
@AutoConfigureSonataHttpClient
@AutoConfigureSonataStubs
@AutoConfigureWebSocketClient
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
}
