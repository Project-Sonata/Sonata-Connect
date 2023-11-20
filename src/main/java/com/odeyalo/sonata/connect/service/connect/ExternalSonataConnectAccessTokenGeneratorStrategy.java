package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.model.User;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface ExternalSonataConnectAccessTokenGeneratorStrategy {

    @NotNull
    Mono<AccessToken> generateAccessToken(@NotNull User generateTo, @NotNull String[] scopes);

    @NotNull
    Mono<AccessToken> generateAccessToken(@NotNull User generateTo, @NotNull Collection<String> scopes);
}
