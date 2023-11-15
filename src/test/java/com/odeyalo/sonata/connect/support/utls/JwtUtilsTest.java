package com.odeyalo.sonata.connect.support.utls;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "hello.hi.bye",
            "something.different.happened",
            "i.love.miku",
    })
    void shouldReturnTrueForValidFormat(String tokenFormat) {
        boolean isValid = JwtUtils.isValidFormat(tokenFormat);

        assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hello.bye",
            "something.different.happened.what",
            "i.love.miku.nakano",
    })
    void shouldReturnFalseForInvalidFormat(String tokenFormat) {
        boolean isValid = JwtUtils.isValidFormat(tokenFormat);

        assertThat(isValid).isFalse();
    }
}