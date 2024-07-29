package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Custom resolver to resolve the {@link SwitchDeviceCommandArgs} from the given {@link ServerWebExchange}.
 * Any parameter with this type will be resolved by this resolver.
 */
@Component
public final class SwitchDeviceCommandArgsResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final @NotNull MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(SwitchDeviceCommandArgs.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {
        return Mono.just(
                // We don't support it now, return default value
                SwitchDeviceCommandArgs.noMatter()
        );
    }
}
