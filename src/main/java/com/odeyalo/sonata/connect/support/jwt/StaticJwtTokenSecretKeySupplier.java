package com.odeyalo.sonata.connect.support.jwt;

import org.springframework.util.Assert;

import javax.crypto.SecretKey;

/**
 * Returns static SecretKey only
 */
public class StaticJwtTokenSecretKeySupplier implements JwtTokenSecretKeySupplier {
    private final SecretKey secretKey;

    public StaticJwtTokenSecretKeySupplier(SecretKey secretKey) {
        Assert.notNull(secretKey, "Secret key cannot be null!");
        this.secretKey = secretKey;
    }

    public static StaticJwtTokenSecretKeySupplier withStaticValue(SecretKey key) {
        return new StaticJwtTokenSecretKeySupplier(key);
    }

    @Override
    public SecretKey get() {
        return secretKey;
    }
}
