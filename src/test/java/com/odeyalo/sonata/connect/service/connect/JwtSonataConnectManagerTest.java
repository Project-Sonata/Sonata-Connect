package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.support.jwt.SecretKeyJwtTokenManager;
import com.odeyalo.sonata.connect.support.utls.JwtUtils;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Objects;

import static com.odeyalo.sonata.connect.support.jwt.StaticJwtTokenSecretKeySupplier.withStaticValue;
import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;

class JwtSonataConnectManagerTest {
    JwtSonataConnectManager testable = new JwtSonataConnectManager(
            new SecretKeyJwtTokenManager(withStaticValue(secretKey)),
            new MockSonataConnectAccessTokenGenerator()
    );


    static final String EXISTING_DEVICE_ID = "miku";
    static final Duration TOKEN_LIFETIME = ofMinutes(15);

    static SecretKey secretKey = generateSecretKey();

    JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();

    @Test
    void shouldReturnScaToken() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldContainScaTokenValue() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> actual.getTokenValue() != null)
                .verifyComplete();
    }

    @Test
    void tokenValueShouldBeJwt() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> JwtUtils.isValidFormat(actual.getTokenValue()))
                .verifyComplete();
    }

    @Test
    void tokenValueShouldBeParseableJwt() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> jwtParser.parse(actual.getTokenValue()) != null)
                .verifyComplete();
    }

    @Test
    void jwtShouldContainIssuedAtClaim() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> parseClaims(actual).getPayload().getIssuedAt() != null)
                .verifyComplete();
    }

    @Test
    void jwtShouldContainExpirationTimeClaim() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> parseClaims(actual).getPayload().getExpiration() != null)
                .verifyComplete();
    }

    @Test
    void jwtShouldHaveLifetime() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> calculateJwtLifetime(actual).equals(TOKEN_LIFETIME))
                .verifyComplete();
    }

    @Test
    void jwtShouldContainTokenIdClaim() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> parseClaims(actual).getPayload().getId() != null)
                .verifyComplete();
    }

    @Test
    void jwtShouldContainDeviceIdClaim() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);
        var user = User.of("123");

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, user)
                .as(StepVerifier::create)
                .expectNextMatches(actual -> parseClaims(actual).getPayload().get("device_id") != null)
                .verifyComplete();
    }

    @Test
    void jwtTokenShouldContainUserId() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, User.of("123"))
                .as(StepVerifier::create)
                .expectNextMatches(actual -> Objects.equals(parseClaims(actual).getPayload().get("user_id"), "123"))
                .verifyComplete();
    }

    @Test
    void shouldContainScaTokenExpirationTime() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, User.of("123"))
                .as(StepVerifier::create)
                .expectNextMatches(actual -> Objects.equals(actual.getTokenLifetime(), TOKEN_LIFETIME))
                .verifyComplete();
    }

    @Test
    void shouldContainPositiveIssuedAt() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);

        testable.generateSCAToken(deviceConnectionAuthenticationTarget, User.of("123"))
                .as(StepVerifier::create)
                .expectNextMatches(actual -> actual.getIssuedAt() > 0)
                .verifyComplete();
    }

    @Test
    void existingTokenShouldBeExchangedForAccessToken() {
        var deviceConnectionAuthenticationTarget = DeviceConnectionAuthenticationTarget.of(EXISTING_DEVICE_ID);

        SCAToken scat = testable.generateSCAToken(deviceConnectionAuthenticationTarget, User.of("123")).block();

        assertThat(scat).isNotNull();

        testable.exchangeForToken(scat.getTokenValue().toString())
                .as(StepVerifier::create)
                .expectNextMatches(actual -> actual.getTokenValue() != null)
                .verifyComplete();
    }

    @Test
    void invalidTokenShouldBeConsideredAsInvalidAndNothingShouldHappen() {
        testable.exchangeForToken("invalid")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    private Duration calculateJwtLifetime(SCAToken actual) {
        Claims claims = parseClaims(actual).getPayload();

        long numericDate = claims.getExpiration().toInstant().getEpochSecond() - claims.getIssuedAt().toInstant().getEpochSecond();

        return Duration.ofSeconds(numericDate);
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

    private Jws<Claims> parseClaims(SCAToken actual) {
        return jwtParser.parseSignedClaims(actual.getTokenValue());
    }
}