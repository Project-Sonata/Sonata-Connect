package com.odeyalo.sonata.connect.support.jwt;

import com.odeyalo.sonata.connect.support.jwt.JwtTokenGenerator.GenerationOptions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collection;
import java.util.Objects;

import static com.odeyalo.sonata.connect.support.jwt.JwtTokenGenerator.GenerationOptions.DefaultClaimsOverridePolicy.*;
import static com.odeyalo.sonata.connect.support.jwt.StaticJwtTokenSecretKeySupplier.withStaticValue;
import static org.assertj.core.api.Assertions.assertThat;

class SecretKeyJwtTokenManagerTest {
    public static final Duration TOKEN_LIFETIME = Duration.ofMinutes(15);
    static SecretKey secretKey = generateSecretKey();

    SecretKeyJwtTokenManager testable = new SecretKeyJwtTokenManager(withStaticValue(secretKey));

    JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();

    @Test
    void shouldGenerateParseableJwt() {
        testable.generateJwt(JwtTokenGenerator.DEFAULT_OPTIONS)
                .as(StepVerifier::create)
                .expectNextMatches(jwtToken -> jwtParser.parse(jwtToken.getTokenValue()) != null)
                .verifyComplete();
    }

    @Test
    void generatedTokenShouldHaveExpirationTime() {
        testable.generateJwt(JwtTokenGenerator.DEFAULT_OPTIONS)
                .as(StepVerifier::create)
                .expectNextMatches(jwtToken -> Objects.equals(calculateTokenDuration(jwtToken), TOKEN_LIFETIME))
                .verifyComplete();
    }

    @Test
    void generatedTokenShouldContainDefaultClaimsOnDoNotOverrideClaims() {
        GenerationOptions generationOptions = GenerationOptions.builder()
                .additionalClaim("waifu", "miku")
                .build();

        testable.generateJwt(generationOptions)
                .as(StepVerifier::create)
                .expectNextMatches(jwtToken -> Objects.equals(parseClaims(jwtToken).get("waifu", String.class), "miku"))
                .verifyComplete();
    }

    @Test
    void generatedTokenShouldContainAdditionalClaims() {
        GenerationOptions generationOptions = GenerationOptions.builder()
                .additionalClaim("waifu", "miku")
                .build();

        testable.generateJwt(generationOptions)
                .as(StepVerifier::create)
                .expectNextMatches(jwtToken -> Objects.equals(parseClaims(jwtToken).get("waifu", String.class), "miku"))
                .verifyComplete();
    }

    @Test
    void generatedTokenShouldContainOverridenClaims() {
        GenerationOptions generationOptions = GenerationOptions.builder()
                .additionalClaim(Claims.ID, "hello")
                .defaultClaimsOverridePolicy(OVERRIDE)
                .build();

        testable.generateJwt(generationOptions)
                .as(StepVerifier::create)
                .expectNextMatches(jwtToken -> {
                    Claims claims = parseClaims(jwtToken);
                    return Objects.equals(claims.get(Claims.ID, String.class), "hello");
                })
                .verifyComplete();
    }

    @Test
    void shouldOverrideDefaultTokenLifetime() {
        Duration lifetime = Duration.ofMinutes(1);
        GenerationOptions generationOptions = GenerationOptions.builder()
                .lifetime(lifetime)
                .build();

        testable.generateJwt(generationOptions)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> Objects.equals(actual.getLifetime(), lifetime))
                .verifyComplete();
    }

    @Test
    void shouldParseJwtAndReturnValidClaims() {
        Collection<String> expectedClaims = JwtTokenGenerator.DEFAULT_CLAIMS;
        JwtToken token = testable.generateJwt(JwtTokenGenerator.DEFAULT_OPTIONS).block();

        assertThat(token).isNotNull();

        testable.parseToken(token.getTokenValue())
                .as(StepVerifier::create)
                .expectNextMatches(actual -> expectedClaims.containsAll(actual.keySet()))
                .verifyComplete();
    }

    @Test
    void shouldParseJwtAndReturnRemainingTime() {
        JwtToken token = testable.generateJwt(JwtTokenGenerator.DEFAULT_OPTIONS).block();

        assertThat(token).isNotNull();

        testable.parseToken(token.getTokenValue())
                .as(StepVerifier::create)
                .expectNextMatches(actual -> actual.getRemainingLifetime().isPositive())
                .verifyComplete();
    }

    private Claims parseClaims(JwtToken jwtToken) {
        return jwtParser.parseSignedClaims(jwtToken.getTokenValue()).getPayload();
    }

    private Duration calculateTokenDuration(JwtToken jwtToken) {
        Claims claims = parseClaims(jwtToken);

        long tokenLifetimeSeconds = claims.getExpiration().toInstant()
                .minusSeconds(claims.getIssuedAt().toInstant().getEpochSecond())
                .getEpochSecond();

        return Duration.ofSeconds(tokenLifetimeSeconds);
    }

    private static SecretKey generateSecretKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("HmacSHA256");
            generator.init(256);
            return generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}