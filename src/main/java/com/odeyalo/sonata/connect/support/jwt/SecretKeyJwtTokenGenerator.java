package com.odeyalo.sonata.connect.support.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static com.odeyalo.sonata.connect.support.jwt.JwtToken.withTokenValue;
import static com.odeyalo.sonata.connect.support.jwt.JwtTokenGenerator.GenerationOptions.DefaultClaimsOverridePolicy.DO_NOT_OVERRIDE;

/**
 * Generate JWT token and sign it with SecretKey
 */
@Component
public class SecretKeyJwtTokenGenerator implements JwtTokenGenerator {
    private final JwtTokenSecretKeySupplier secretKeySupplier;

    public SecretKeyJwtTokenGenerator(JwtTokenSecretKeySupplier secretKeySupplier) {
        this.secretKeySupplier = secretKeySupplier;
    }

    @Override
    @NotNull
    public Mono<JwtToken> generateJwt(@NotNull GenerationOptions options) {
        Instant generationTime = Instant.now();

        Date issuedAt = Date.from(generationTime);
        Date expiresIn = Date.from(generationTime.plusSeconds(options.getLifetime().toSeconds()));

        JwtBuilder jwtBuilder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuedAt(issuedAt)
                .expiration(expiresIn)
                .signWith(secretKeySupplier.get());

        if ( options.getDefaultClaimsOverridePolicy() == DO_NOT_OVERRIDE ) {
            removeDefaultClaimsFromAdditional(options);
        }

        jwtBuilder.claims().add(options.getAdditionalClaims());

        JwtToken jwtToken = convertToJwtToken(options, expiresIn, jwtBuilder);

        return Mono.just(jwtToken);
    }

    private static void removeDefaultClaimsFromAdditional(@NotNull GenerationOptions options) {
        DEFAULT_CLAIMS.forEach(options.getAdditionalClaims().keySet()::remove);
    }

    private static JwtToken convertToJwtToken(@NotNull GenerationOptions options, Date expiresIn, JwtBuilder jwtBuilder) {
        return withTokenValue(jwtBuilder.compact())
                .lifetime(options.getLifetime())
                .expiresIn(expiresIn.toInstant().getEpochSecond())
                .build();
    }
}
