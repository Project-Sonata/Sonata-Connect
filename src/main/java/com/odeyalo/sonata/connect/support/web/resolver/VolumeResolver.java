package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.exception.InvalidVolumeException;
import com.odeyalo.sonata.connect.model.Volume;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Resolves a {@link Volume} from the given {@link ServerWebExchange}
 */
@Component
public final class VolumeResolver implements HandlerMethodArgumentResolver {
    private final Mono<Object> INVALID_VOLUME_ERROR = Mono.error(
            InvalidVolumeException.withCustomMessage("Volume required to be: 0-100")
    );

    @Override
    public boolean supportsParameter(@NotNull final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Volume.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {

        final String volumePercent = exchange.getRequest().getQueryParams().getFirst("volume_percent");

        if ( !NumberUtils.isParsable(volumePercent) ) {
            return INVALID_VOLUME_ERROR;
        }

        final int volume = NumberUtils.createInteger(volumePercent);

        if ( volume < 0 || volume > 100 ) {
            return INVALID_VOLUME_ERROR;
        }

        return Mono.just(
                Volume.fromInt(volume)
        );
    }
}
