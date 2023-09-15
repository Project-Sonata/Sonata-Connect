package com.odeyalo.sonata.connect.config.security.configurer;

import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Matches only websocket
 */
@Component
public class WebSocketServerWebExchangeMatcher implements ServerWebExchangeMatcher {
    public static final String WEB_SOCKET_PATH = "/player/sync/**";

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return ServerWebExchangeMatchers.pathMatchers(WEB_SOCKET_PATH).matches(exchange);
    }
}
