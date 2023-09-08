package com.odeyalo.sonata.connect.exception;

import com.odeyalo.sonata.connect.service.player.handler.SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate;

/**
 * Thrown by {@link SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate} if more than 1 device was received
 */
public class MultipleTargetDevicesNotSupportedException extends InvalidTargetDevicesException {
    public static final String REASON_CODE = "multiple_devices_not_supported";
    public static final String DEFAULT_MESSAGE = "One and only one deviceId should be provided. More than one is not supported now";

    public static MultipleTargetDevicesNotSupportedException defaultException() {
        return new MultipleTargetDevicesNotSupportedException();
    }

    public static MultipleTargetDevicesNotSupportedException withCustomMessage(String message) {
        return new MultipleTargetDevicesNotSupportedException(message);
    }

    public static MultipleTargetDevicesNotSupportedException withMessageAndCause(String message, Throwable cause) {
        return new MultipleTargetDevicesNotSupportedException(message, cause);
    }

    public MultipleTargetDevicesNotSupportedException() {
        super(DEFAULT_MESSAGE, REASON_CODE);
    }

    public MultipleTargetDevicesNotSupportedException(String message) {
        super(message, REASON_CODE);
    }

    public MultipleTargetDevicesNotSupportedException(String message, Throwable cause) {
        super(message, cause, REASON_CODE);
    }

    public MultipleTargetDevicesNotSupportedException(Throwable cause) {
        super(cause, REASON_CODE);
    }

    public MultipleTargetDevicesNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, REASON_CODE);
    }
}
