package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.support.jwt.JwtToken;
import com.odeyalo.sonata.connect.support.jwt.JwtTokenGenerator;
import com.odeyalo.sonata.connect.support.jwt.JwtTokenGenerator.GenerationOptions.DefaultClaimsOverridePolicy;
import com.odeyalo.sonata.connect.support.utls.JwtUtils;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

import static com.odeyalo.sonata.connect.service.connect.SCAToken.withTokenValue;

public class JwtSonataConnectManager implements SonataConnectManager {
    private final JwtTokenGenerator generator;

    public JwtSonataConnectManager(JwtTokenGenerator generator) {
        this.generator = generator;
    }

    @NotNull
    @Override
    public Mono<SCAToken> generateSCAToken(DeviceConnectionAuthenticationTarget connectionAuthenticationTarget) {
        Map<String, Object> claims = Collections.singletonMap(
                "device_id", connectionAuthenticationTarget.getId()
        );

        Mono<JwtToken> jwtTokenMono = generator.generateJwt(
                JwtTokenGenerator.GenerationOptions.builder()
                        .additionalClaims(claims)
                        .defaultClaimsOverridePolicy(DefaultClaimsOverridePolicy.OVERRIDE)
                        .build()
        );

        return jwtTokenMono.map(jwtToken -> withTokenValue(jwtToken.getTokenValue().tokenValue()))
                .log();
    }

    @NotNull
    @Override
    public Mono<AccessToken> exchangeForToken(@NotNull String scat) {
        if ( JwtUtils.isInvalidFormat(scat) ) {
            return Mono.empty();
        }
        return Mono.just(AccessToken.of("value"));
    }
}
