package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.exception.InvalidSeekPositionException;
import com.odeyalo.sonata.connect.exception.UnsupportedSeekPositionPrecisionException;
import com.odeyalo.sonata.connect.service.player.SeekPosition;
import com.odeyalo.sonata.connect.service.player.SeekPosition.Precision;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Resolves a {@link SeekPosition} from the given {@link ServerWebExchange},
 * returns an error if request missing required parameters or parameters are invalid
 */
@Component
public final class SeekPositionResolver implements HandlerMethodArgumentResolver {
    private static final String POSITION_QUERY_PARAM = "position";
    private static final String PRECISION_QUERY_PARAM = "precision";
    private static final String DEFAULT_PRECISION = "millis";

    @Override
    public boolean supportsParameter(@NotNull final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(SeekPosition.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {

        final Map<String, String> queryParams = exchange.getRequest().getQueryParams().toSingleValueMap();

        final String positionValue = queryParams.get(POSITION_QUERY_PARAM);

        if ( !NumberUtils.isParsable(positionValue) ) {
            return Mono.error(InvalidSeekPositionException.defaultException());
        }

        final String precisionValue = queryParams.getOrDefault(PRECISION_QUERY_PARAM, DEFAULT_PRECISION);

        if ( !EnumUtils.isValidEnumIgnoreCase(Precision.class, precisionValue) ) {
            return Mono.error(new UnsupportedSeekPositionPrecisionException(precisionValue));
        }

        final int position = NumberUtils.toInt(positionValue);
        final Precision precision = EnumUtils.getEnumIgnoreCase(Precision.class, precisionValue);

        return Mono.just(SeekPosition.fromPrecision(position, precision));
    }
}
