package com.odeyalo.sonata.connect.exception;

/**
 * Exception to throw when there is no target device provided.
 */
public class TargetDeviceRequiredException extends InvalidTargetDevicesException {
    public static final String REASON_CODE = "target_device_required";
    public static final String DEFAULT_MESSAGE = "Target device is required!";

    public static TargetDeviceRequiredException defaultException() {
        return new TargetDeviceRequiredException();
    }

    public static TargetDeviceRequiredException withCustomMessage(String message) {
        return new TargetDeviceRequiredException(message);
    }

    public static TargetDeviceRequiredException withMessageAndCause(String message, Throwable cause) {
        return new TargetDeviceRequiredException(message, cause);
    }

    public TargetDeviceRequiredException() {
        super(DEFAULT_MESSAGE, REASON_CODE);
    }

    public TargetDeviceRequiredException(String message) {
        super(message, REASON_CODE);
    }

    public TargetDeviceRequiredException(String message, Throwable cause) {
        super(message, cause, REASON_CODE);
    }

    public TargetDeviceRequiredException(Throwable cause) {
        super(cause, REASON_CODE);
    }

    public TargetDeviceRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, REASON_CODE);
    }
}
