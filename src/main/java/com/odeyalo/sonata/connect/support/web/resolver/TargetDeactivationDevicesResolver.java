package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public final class TargetDeactivationDevicesResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(TargetDeactivationDevices.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {
        return Mono.just(
                TargetDeactivationDevices.empty()
        );
    }
}
