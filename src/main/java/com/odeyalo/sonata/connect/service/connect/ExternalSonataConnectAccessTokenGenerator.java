package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ExternalSonataConnectAccessTokenGenerator implements SonataConnectAccessTokenGenerator {
    private final ExternalSonataConnectAccessTokenGeneratorStrategy accessTokenGeneratorStrategy;
    // TODO: Change it
    private final String[] SCOPES = new String[]{
            "streaming",
            "user-library-read"
    };

    public ExternalSonataConnectAccessTokenGenerator(ExternalSonataConnectAccessTokenGeneratorStrategy accessTokenGeneratorStrategy) {
        this.accessTokenGeneratorStrategy = accessTokenGeneratorStrategy;
    }

    @NotNull
    @Override
    public Mono<AccessToken> generateAccessToken(@NotNull User user) {
        return accessTokenGeneratorStrategy.generateAccessToken(user, SCOPES);
    }
}
