package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.dto.DeviceSwitchRequest;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.support.web.annotation.TransferPlaybackTargets;
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

/**
 * Custom {@link org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver} that used to resolve {@link TargetDevices} from the given {@link ServerWebExchange}.
 * Only parameters annotated with {@link TransferPlaybackTargets} will be handled.
 */
@Component
public final class TransferPlaybackDevicesResolver extends AbstractMessageReaderArgumentResolver {

    public TransferPlaybackDevicesResolver(final List<HttpMessageReader<?>> readers) {
        super(readers);
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(TargetDevices.class) &&
                parameter.hasParameterAnnotation(TransferPlaybackTargets.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {

        final Parameter bodyTypeParameter = resolveBodyTypeParameter();

        return readBody(MethodParameter.forParameter(bodyTypeParameter), parameter, true, bindingContext, exchange)
                .cast(DeviceSwitchRequest.class)
                .map(DeviceSwitchRequest::getDeviceIds)
                .map(TargetDevices::fromDeviceIds);
    }

    private Parameter resolveBodyTypeParameter() {
        //noinspection DataFlowIssue never be null because we always have method in this class
        return ReflectionUtils
                .findMethod(this.getClass(), "methodParameterDescriptorSupport", DeviceSwitchRequest.class)
                .getParameters()[0];
    }
    /**
     * Support method to resolve {@link Parameter} to set in {@link MethodParameter}
     * DO NOT DELETE IT IN ANY CASE, ONLY IF YOU FOUND A BETTER SOLUTION
     *
     * @param body - body class to read content to
     */
    @SuppressWarnings("unused")
    private void methodParameterDescriptorSupport(@Valid DeviceSwitchRequest body) {
    }
}
