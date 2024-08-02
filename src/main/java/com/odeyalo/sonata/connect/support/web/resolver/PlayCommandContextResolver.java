package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.dto.PlayResumePlaybackRequest;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.annotation.AbstractMessageReaderArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.function.Function;

/**
 * Resolves a {@link PlayCommandContext} from the given {@link ServerWebExchange}
 */
@Component
public final class PlayCommandContextResolver extends AbstractMessageReaderArgumentResolver {

    public PlayCommandContextResolver(final List<HttpMessageReader<?>> readers) {
        super(readers);
    }

    @Override
    public boolean supportsParameter(@NotNull final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(PlayCommandContext.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {

        final Parameter bodyTypeParameter = resolveBodyTypeParameter();

        return readBody(MethodParameter.forParameter(bodyTypeParameter), parameter, false, bindingContext, exchange)
                .cast(PlayResumePlaybackRequest.class)
                .flatMap(PlayCommandContextResolver::tryParseCommandRequest)
                .defaultIfEmpty(PlayCommandContext.resumePlayback())
                // we need this because java can't recognize type correctly :(
                .map(Function.identity());
    }

    @NotNull
    private static Mono<PlayCommandContext> tryParseCommandRequest(final PlayResumePlaybackRequest playResumePlaybackRequest) {
        final String contextUriStr = playResumePlaybackRequest.getContextUri();

        if ( ContextUri.isValid(contextUriStr) ) {
            return Mono.just(PlayCommandContext.from(
                    ContextUri.fromString(contextUriStr)
            ));
        }

        return Mono.defer(() -> Mono.error(
                new ReasonCodeAwareMalformedContextUriException("Context uri is malformed", contextUriStr)
        ));
    }

    @NotNull
    private Parameter resolveBodyTypeParameter() {
        //noinspection DataFlowIssue never be null because we always have method in this class
        return ReflectionUtils
                .findMethod(this.getClass(), "methodParameterDescriptorSupport", PlayResumePlaybackRequest.class)
                .getParameters()[0];
    }

    /**
     * Support method to resolve {@link Parameter} to set in {@link MethodParameter}
     * DO NOT DELETE IT IN ANY CASE, ONLY IF YOU FOUND A BETTER SOLUTION
     *
     * @param body - body class to read content to
     */
    @SuppressWarnings("unused")
    private void methodParameterDescriptorSupport(@Valid PlayResumePlaybackRequest body) {
    }
}
