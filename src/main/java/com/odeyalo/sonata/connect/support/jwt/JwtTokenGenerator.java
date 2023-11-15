package com.odeyalo.sonata.connect.support.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Generate JWT tokens only.
 */
public interface JwtTokenGenerator {
    /**
     * Generate jwt token and return it
     * @param options - options to generate JWT token with
     * @return - mono with JwtToken
     */
    @NotNull
    Mono<JwtToken> generateJwt(@NotNull GenerationOptions options);

    @Value
    @AllArgsConstructor(staticName = "of")
    @Builder
    class GenerationOptions {
        Map<String, Object> additionalClaims;
        DefaultClaimsOverridePolicy defaultClaimsOverridePolicy;
        Duration lifetime;

        public enum DefaultClaimsOverridePolicy {
            OVERRIDE,
            DO_NOT_OVERRIDE
        }
    }
}
