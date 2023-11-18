package com.odeyalo.sonata.connect.service.connect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.Duration;

import static java.time.Duration.ofMinutes;

/**
 * Sonata connect authentication token is used to authenticate the device in secure way without using password,
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class SCAToken {
    CharSequence tokenValue;
    long issuedAt = System.currentTimeMillis();
    Duration tokenLifetime = DEFAULT_TOKEN_LIFETIME;

    public static final Duration DEFAULT_TOKEN_LIFETIME = ofMinutes(15);

    public static SCAToken withTokenValue(CharSequence tokenValue) {
        return SCAToken.builder().tokenValue(tokenValue).build();
    }
}
