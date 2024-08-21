package com.odeyalo.sonata.connect.config.security.configurer;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.stereotype.Component;

/**
 * Helper class to configure the {@link AuthorizeExchangeSpec}
 */
@Component
public final class AuthorizeExchangeSpecConfigurer implements Customizer<AuthorizeExchangeSpec> {
    private static final String SCA_TOKEN_EXCHANGE_ENDPOINT = "/connect/auth/exchange**";

    @Override
    public void customize(AuthorizeExchangeSpec authorizeExchangeSpec) {
        authorizeExchangeSpec
                .pathMatchers(SCA_TOKEN_EXCHANGE_ENDPOINT).permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated();
    }
}
