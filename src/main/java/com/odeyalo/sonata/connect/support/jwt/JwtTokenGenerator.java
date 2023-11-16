package com.odeyalo.sonata.connect.support.jwt;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.odeyalo.sonata.connect.support.jwt.JwtTokenGenerator.GenerationOptions.DefaultClaimsOverridePolicy.DO_NOT_OVERRIDE;

/**
 * Generate JWT tokens only.
 */
public interface JwtTokenGenerator {

    GenerationOptions DEFAULT_OPTIONS = GenerationOptions.useDefault();

    Collection<String> DEFAULT_CLAIMS = List.of(Claims.ID, Claims.ISSUED_AT, Claims.EXPIRATION);

    /**
     * Generate jwt token and return it
     *
     * @param options - options to generate JWT token with
     * @return - mono with JwtToken
     */
    @NotNull
    Mono<JwtToken> generateJwt(@NotNull GenerationOptions options);

    @Value
    @AllArgsConstructor(staticName = "of")
    @Builder
    class GenerationOptions {
        @Singular
        Map<String, Object> additionalClaims;
        @Builder.Default
        DefaultClaimsOverridePolicy defaultClaimsOverridePolicy = DO_NOT_OVERRIDE;
        @Builder.Default
        Duration lifetime = Duration.ofMinutes(15);

        public static GenerationOptions useDefault() {
            return builder().build();
        }

        public enum DefaultClaimsOverridePolicy {
            OVERRIDE,
            DO_NOT_OVERRIDE
        }
    }
}
