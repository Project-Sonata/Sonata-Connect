package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.support.jwt.JwtToken;
import com.odeyalo.sonata.connect.support.jwt.JwtToken.JwtTokenValue;
import com.odeyalo.sonata.connect.support.jwt.JwtTokenGenerator;
import com.odeyalo.sonata.connect.support.jwt.JwtTokenManager;
import com.odeyalo.sonata.connect.support.jwt.ParsedJwtTokenMetadata;
import com.odeyalo.sonata.connect.support.utls.JwtUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.odeyalo.sonata.connect.service.connect.SCAToken.withTokenValue;

@Component
public class JwtSonataConnectManager implements SonataConnectManager {
    private final JwtTokenManager jwtTokenManager;
    private final SonataConnectAccessTokenGenerator accessTokenGenerator;

    private static final String DEVICE_ID_CLAIM = "device_id";
    private static final String USER_ID_CLAIM = "user_id";

    @Autowired
    public JwtSonataConnectManager(JwtTokenManager jwtTokenManager, SonataConnectAccessTokenGenerator accessTokenGenerator) {
        this.jwtTokenManager = jwtTokenManager;
        this.accessTokenGenerator = accessTokenGenerator;
    }

    @NotNull
    @Override
    public Mono<SCAToken> generateSCAToken(DeviceConnectionAuthenticationTarget connectionAuthenticationTarget, User user) {

        Map<String, Object> claims = new HashMap<>() {{
            put(DEVICE_ID_CLAIM, connectionAuthenticationTarget.getId());
            put(USER_ID_CLAIM, user.getId());
        }};

        var generationOptions = JwtTokenGenerator.GenerationOptions.builder()
                .additionalClaims(claims)
                .build();

        Mono<JwtToken> jwtTokenMono = jwtTokenManager.generateJwt(generationOptions);

        return jwtTokenMono.map(jwtToken -> withTokenValue(jwtToken.getTokenValue().tokenValue()));
    }

    @NotNull
    @Override
    public Mono<AccessToken> exchangeForToken(@NotNull String scat) {
        if ( JwtUtils.isInvalidFormat(scat) ) {
            return Mono.empty();
        }
        return jwtTokenManager.parseToken(JwtTokenValue.just(scat))
                .map(JwtSCATokenMetadataWrapper::new)
                .flatMap(tokenMetadata -> accessTokenGenerator.generateAccessToken(tokenMetadata.getUser()));
    }


    private record JwtSCATokenMetadataWrapper(@NotNull ParsedJwtTokenMetadata parent) {

        public User getUser() {
            String userId = get(USER_ID_CLAIM, String.class);
            return User.of(userId);
        }

        public DeviceConnectionAuthenticationTarget getTargetDevice() {
            String deviceId = get(DEVICE_ID_CLAIM, String.class);
            return DeviceConnectionAuthenticationTarget.of(deviceId);
        }

        // Delegate methods

        public Duration getRemainingLifetime() {
            return parent.getRemainingLifetime();
        }

        public int size() {
            return parent.size();
        }

        public boolean isEmpty() {
            return parent.isEmpty();
        }

        public boolean containsKey(String key) {
            return parent.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return parent.containsValue(value);
        }

        public Set<String> keySet() {
            return parent.keySet();
        }

        public Collection<Object> values() {
            return parent.values();
        }

        public Object get(String key) {
            return parent.get(key);
        }

        public <T> T get(String key, Class<T> requiredClass) {
            return parent.get(key, requiredClass);
        }
    }
}
