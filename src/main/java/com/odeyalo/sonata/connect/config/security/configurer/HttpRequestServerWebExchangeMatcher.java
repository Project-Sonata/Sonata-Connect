package com.odeyalo.sonata.connect.config.security.configurer;

import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Match only http requests
 */
@Component
public class HttpRequestServerWebExchangeMatcher implements ServerWebExchangeMatcher {
    private final WebSocketServerWebExchangeMatcher webSocketServerWebExchangeMatcher;

    public HttpRequestServerWebExchangeMatcher(WebSocketServerWebExchangeMatcher webSocketServerWebExchangeMatcher) {
        this.webSocketServerWebExchangeMatcher = webSocketServerWebExchangeMatcher;
    }

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return new NegatedServerWebExchangeMatcher(webSocketServerWebExchangeMatcher).matches(exchange);
    }
}
