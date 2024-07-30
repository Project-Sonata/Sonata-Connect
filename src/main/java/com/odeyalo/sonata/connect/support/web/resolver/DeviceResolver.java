package com.odeyalo.sonata.connect.support.web.resolver;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.service.support.mapper.dto.ConnectDeviceRequest2DeviceConverter;
import com.odeyalo.sonata.connect.support.web.annotation.ConnectionTarget;
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
 * A custom resolver that resolves a {@link Device} from the given {@link ServerWebExchange}.
 * Only methods of type {@link Device} annotated with {@link ConnectionTarget} will be processed.
 */
@Component
public final class DeviceResolver extends AbstractMessageReaderArgumentResolver {
    private final ConnectDeviceRequest2DeviceConverter deviceModelConverter;

    public DeviceResolver(final List<HttpMessageReader<?>> readers,
                          final ConnectDeviceRequest2DeviceConverter deviceModelConverter) {
        super(readers);
        this.deviceModelConverter = deviceModelConverter;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Device.class) &&
                parameter.hasParameterAnnotation(ConnectionTarget.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull final MethodParameter parameter,
                                        @NotNull final BindingContext bindingContext,
                                        @NotNull final ServerWebExchange exchange) {

        final Parameter bodyTypeParameter = resolveBodyTypeParameter();

        return readBody(MethodParameter.forParameter(bodyTypeParameter), parameter, true, bindingContext, exchange)
                .cast(ConnectDeviceRequest.class)
                .map(deviceModelConverter::convertTo);
    }

    private Parameter resolveBodyTypeParameter() {
        //noinspection DataFlowIssue never be null because we always have method in this class
        return ReflectionUtils
                .findMethod(this.getClass(), "methodParameterDescriptorSupport", ConnectDeviceRequest.class)
                .getParameters()[0];
    }
    /**
     * Support method to resolve {@link Parameter} to set in {@link MethodParameter}
     * DO NOT DELETE IT IN ANY CASE, ONLY IF YOU FOUND A BETTER SOLUTION
     *
     * @param body - body class to read content to
     */
    @SuppressWarnings("unused")
    private void methodParameterDescriptorSupport(@Valid ConnectDeviceRequest body) {
    }
}
