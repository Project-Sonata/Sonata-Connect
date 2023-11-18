package com.odeyalo.sonata.connect.config;

import com.odeyalo.sonata.connect.support.jwt.JwtTokenSecretKeySupplier;
import com.odeyalo.sonata.connect.support.jwt.StaticJwtTokenSecretKeySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

@Configuration
public class JwtTokenOptionsConfiguration {

    @Bean
    public JwtTokenSecretKeySupplier jwtTokenSecretKeySupplier() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("HmacSHA256");
        generator.init(256);
        SecretKey secretKey = generator.generateKey();

        return StaticJwtTokenSecretKeySupplier.withStaticValue(secretKey);
    }
}
