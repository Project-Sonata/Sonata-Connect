package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.model.ShuffleMode;
import org.apache.commons.lang.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public final class ShuffleModeResolver implements HandlerMethodArgumentResolver {

    public static final String STATE_QUERY_PARAMETER_KEY = "state";

    @Override
    public boolean supportsParameter(@NotNull final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(ShuffleMode.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {

        final String state = exchange.getRequest().getQueryParams().getFirst(STATE_QUERY_PARAMETER_KEY);

        final Boolean booleanState = BooleanUtils.toBooleanObject(state);

        if ( booleanState == null ) {
            return handleMissingStateValue(parameter);
        }

        return Mono.just(booleanState ? ShuffleMode.ENABLED : ShuffleMode.OFF);
    }

    private Mono<Object> handleMissingStateValue(@NotNull final MethodParameter parameter) {

        return Mono.defer(() -> Mono.error(new MissingRequestValueException(
                STATE_QUERY_PARAMETER_KEY, parameter.getNestedParameterType(),
                String.format("Missing '%s' query parameter", STATE_QUERY_PARAMETER_KEY),
                parameter)
        ));
    }
}
