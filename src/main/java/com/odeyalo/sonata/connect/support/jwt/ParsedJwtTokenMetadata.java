package com.odeyalo.sonata.connect.support.jwt;

import lombok.*;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Metadata about token that has been parsed
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class ParsedJwtTokenMetadata {
    @Getter(value = AccessLevel.NONE)
    @Singular
    Map<String, Object> claims;
    Duration remainingLifetime;

    public int size() {
        return claims.size();
    }

    public boolean isEmpty() {
        return claims.isEmpty();
    }

    public boolean containsKey(String key) {
        return claims.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return claims.containsValue(value);
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(claims.keySet());
    }

    public Collection<Object> values() {
        return Collections.unmodifiableCollection(claims.values());
    }

    public Object get(String key) {
        return claims.get(key);
    }

    public <T> T get(String key, Class<T> requiredClass) {
        return requiredClass.cast(claims.get(key));
    }
}
