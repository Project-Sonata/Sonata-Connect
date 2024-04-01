package com.odeyalo.sonata.connect.config.security.configurer;

import com.odeyalo.suite.security.auth.TokenAuthenticationManager;
import com.odeyalo.suite.security.auth.token.AccessTokenMetadata;
import com.odeyalo.suite.security.auth.token.ValidatedAccessToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Objects;

/**
 * Helper class to configure the {@link AuthorizeExchangeSpec}
 */
@Component
public class AuthorizeExchangeSpecConfigurer implements Customizer<AuthorizeExchangeSpec> {
    private static final String SCA_TOKEN_EXCHANGE_ENDPOINT = "/connect/auth/exchange**";

    @Override
    public void customize(AuthorizeExchangeSpec authorizeExchangeSpec) {
        authorizeExchangeSpec
                .pathMatchers(SCA_TOKEN_EXCHANGE_ENDPOINT).permitAll()
                .anyExchange().authenticated();
    }


    @Bean
    @Primary
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new TokenAuthenticationManager(
                tokenValue -> {
                    if ( Objects.equals(tokenValue, "token1") ) {
                        return Mono.just(
                                ValidatedAccessToken.valid(
                                        AccessTokenMetadata.of("123", new String[]{"read", "write"},
                                                Instant.now().getEpochSecond(),
                                                Instant.now().plusSeconds(300).getEpochSecond()
                                        ))
                        );
                    }
                    if ( Objects.equals(tokenValue, "token1_2") ) {
                        return Mono.just(ValidatedAccessToken.valid(
                                AccessTokenMetadata.of("123", new String[]{"read", "write", "playlist"},
                                        Instant.now().getEpochSecond(),
                                        Instant.now().plusSeconds(300).getEpochSecond()
                                )));
                    }

                    if ( Objects.equals(tokenValue, "token2_2") ) {
                        return Mono.just(
                                ValidatedAccessToken.valid(
                                        AccessTokenMetadata.of("miku", new String[]{"read", "write", "playlist"},
                                                Instant.now().getEpochSecond(),
                                                Instant.now().plusSeconds(300).getEpochSecond()
                                        )));
                    }

                    return Mono.just(ValidatedAccessToken.invalid());
                }
        );
    }
}
