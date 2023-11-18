package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Mock impl of SonataConnectAccessTokenGenerator
 */
@Component
public class MockSonataConnectAccessTokenGenerator implements SonataConnectAccessTokenGenerator {

    @Override
    @NotNull
    public Mono<AccessToken> generateAccessToken(@NotNull User user) {
        return Mono.just(AccessToken.of("valid_access_token"));
    }
}
