package com.odeyalo.sonata.connect.config;

import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Web filter that used to create empty player state if it is missing for the user
 */
@Component
public class EmptyPlayerStateCreatorOnMissingWebFilter implements WebFilter {

    final BasicPlayerOperations playerOperations;

    public EmptyPlayerStateCreatorOnMissingWebFilter(BasicPlayerOperations playerOperations) {
        this.playerOperations = playerOperations;
    }

    @Override
    @NotNull
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .cast(AuthenticatedUser.class)
                .map(user -> User.of(user.getDetails().getId()))
                .flatMap(playerOperations::createState)
                .then(chain.filter(exchange));
    }
}
