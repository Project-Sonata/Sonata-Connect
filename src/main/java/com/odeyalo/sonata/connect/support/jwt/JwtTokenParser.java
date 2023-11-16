package com.odeyalo.sonata.connect.support.jwt;

import com.odeyalo.sonata.connect.support.jwt.JwtToken.JwtTokenValue;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface JwtTokenParser {
    /**
     * Parse JWT token value and returns metadata about token
     * @param jwtTokenValue - value to parse
     * @return - parsed token metadata or empty mono
     */
    @NotNull
    Mono<ParsedJwtTokenMetadata> parseToken(@NotNull JwtTokenValue jwtTokenValue);

}
