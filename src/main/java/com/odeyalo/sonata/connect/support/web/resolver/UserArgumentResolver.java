package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Simple class that populates the WEB handler method with {@link User}
 */
@Component
public final class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(User.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter,
                                        @NotNull BindingContext bindingContext,
                                        @NotNull ServerWebExchange exchange) {

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(AuthenticatedUser.class)
                .map(UserArgumentResolver::toUser);
    }

    private static User toUser(AuthenticatedUser authenticatedUser) {
        return User.of(authenticatedUser.getDetails().getId());
    }
}
