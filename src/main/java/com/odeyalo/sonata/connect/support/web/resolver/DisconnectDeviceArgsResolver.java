package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.exception.web.MissingRequestParameterException;
import com.odeyalo.sonata.connect.service.player.DisconnectDeviceArgs;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public final class DisconnectDeviceArgsResolver implements HandlerMethodArgumentResolver {

    public static final String DEVICE_ID_KEY = "device_id";

    @Override
    public boolean supportsParameter(@NotNull final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(DisconnectDeviceArgs.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {

        final String deviceId = exchange.getRequest().getQueryParams().getFirst(DEVICE_ID_KEY);

        if ( StringUtils.isBlank(deviceId) ) {
            return Mono.defer(() -> Mono.error(
                    new MissingRequestParameterException("'device_id' parameter is required")
            ));
        }

        return Mono.just(
                DisconnectDeviceArgs.withDeviceId(deviceId)
        );
    }
}
