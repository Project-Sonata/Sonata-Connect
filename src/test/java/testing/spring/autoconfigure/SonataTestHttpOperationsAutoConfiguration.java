package testing.spring.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import testing.shared.WebTestClientSonataTestHttpOperations;

/**
 * Auto-configuration for SonataTestHttpOperations
 *
 * @see AutoConfigureSonataHttpClient
 */
public class SonataTestHttpOperationsAutoConfiguration {

    @Bean
    public WebTestClientSonataTestHttpOperations webTestClientSonataTestHttpOperations(WebTestClient webTestClient) {
        return new WebTestClientSonataTestHttpOperations(webTestClient);
    }
}