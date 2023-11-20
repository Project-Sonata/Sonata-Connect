package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.suite.reactive.client.ReactiveInternalTokenClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class SuiteExternalSonataConnectAccessTokenGeneratorStrategy implements ExternalSonataConnectAccessTokenGeneratorStrategy {
    private final ReactiveInternalTokenClient tokenClient;
    private static final String DELIMITER = " ";

    public SuiteExternalSonataConnectAccessTokenGeneratorStrategy(ReactiveInternalTokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @NotNull
    @Override
    public Mono<AccessToken> generateAccessToken(@NotNull User generateTo, @NotNull String[] scopes) {
        return tokenClient.generateInternalToken(generateTo.getId(), String.join(DELIMITER, scopes))
                .flatMap(resp -> resp.getBody() != null ? resp.getBody() : Mono.empty())
                .map(body -> AccessToken.of(body.getAccessToken()));
    }

    @NotNull
    @Override
    public Mono<AccessToken> generateAccessToken(@NotNull User generateTo, @NotNull Collection<String> scopes) {
        return generateAccessToken(generateTo, scopes.toArray(String[]::new));
    }
}
