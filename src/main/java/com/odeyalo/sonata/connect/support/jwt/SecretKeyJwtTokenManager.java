package com.odeyalo.sonata.connect.support.jwt;

import com.odeyalo.sonata.connect.support.jwt.JwtToken.JwtTokenValue;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static com.odeyalo.sonata.connect.support.jwt.JwtToken.withTokenValue;
import static com.odeyalo.sonata.connect.support.jwt.JwtTokenGenerator.GenerationOptions.DefaultClaimsOverridePolicy.DO_NOT_OVERRIDE;

/**
 * Generate JWT token and sign it with SecretKey
 */
@Component
public class SecretKeyJwtTokenManager implements JwtTokenManager {
    private final JwtTokenSecretKeySupplier secretKeySupplier;

    public SecretKeyJwtTokenManager(JwtTokenSecretKeySupplier secretKeySupplier) {
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

    @Override
    @NotNull
    public Mono<ParsedJwtTokenMetadata> parseToken(@NotNull JwtTokenValue jwtTokenValue) {
        return Mono.fromCallable(() -> {
            JwtParser parser = Jwts.parser().verifyWith(secretKeySupplier.get()).build();
            Claims claims = parser.parseSignedClaims(jwtTokenValue.tokenValue()).getPayload();

            Instant remainingLifetime = calculateRemainingLifetime(claims);
            return ParsedJwtTokenMetadata.of(claims, Duration.ofMinutes(remainingLifetime.getEpochSecond()));
        });
    }

    private static JwtToken convertToJwtToken(@NotNull GenerationOptions options, Date expiresIn, JwtBuilder jwtBuilder) {
        return withTokenValue(jwtBuilder.compact())
                .lifetime(options.getLifetime())
                .expiresIn(expiresIn.toInstant().getEpochSecond())
                .build();
    }

    private static void removeDefaultClaimsFromAdditional(@NotNull GenerationOptions options) {
        DEFAULT_CLAIMS.forEach(options.getAdditionalClaims().keySet()::remove);
    }

    private static Instant calculateRemainingLifetime(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.toInstant().minusSeconds(LocalDateTime.now().getSecond());
    }
}
