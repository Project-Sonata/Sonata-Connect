package com.odeyalo.sonata.connect.support.jwt;

import com.odeyalo.sonata.connect.support.DefaultCharSequence;
import com.odeyalo.sonata.connect.support.utls.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * Contains jwt token value and basic metadata about it(like lifetime and expires_in)
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class JwtToken {
    @NotNull
    @NonNull
    JwtTokenValue tokenValue;
    long expiresIn;
    @NotNull
    @NonNull
    Duration lifetime;

    private static JwtToken.JwtTokenBuilder builder() {
        return new JwtTokenBuilder();
    }

    public static JwtToken.JwtTokenBuilder withTokenValue(CharSequence charSequence) {
        return builder().tokenValue(JwtTokenValue.just(charSequence));
    }

    /**
     * Wrapper for token value that validated before it set.
     *
     * @param tokenValue - value to store
     */
    public record JwtTokenValue(CharSequence tokenValue) implements DefaultCharSequence {

        public JwtTokenValue {
            Assert.notNull(tokenValue, "Value cannot be null!");
            Assert.state(JwtUtils.isValidFormat(tokenValue), "Jwt token must be valid format!");
        }

        public static JwtTokenValue just(CharSequence value) {
            return new JwtTokenValue(value);
        }

        @Override
        @NotNull
        public String toString() {
            return tokenValue.toString();
        }
    }
}
