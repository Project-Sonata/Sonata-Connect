package com.odeyalo.sonata.connect.exception;

/**
 * Exception to throw when no device with ID or by condition was not found
 */
public class DeviceNotFoundException extends RuntimeException implements ReasonCodeAware {
    public static final String DEFAULT_MESSAGE = "Device with provided ID was not found!";
    public static final String REASON_CODE = "device_not_found";

    public static DeviceNotFoundException defaultException() {
        return new DeviceNotFoundException();
    }

    public static DeviceNotFoundException withCustomMessage(String message) {
        return new DeviceNotFoundException(message);
    }

    public static DeviceNotFoundException withMessageAndCause(String message, Throwable cause) {
        return new DeviceNotFoundException(message, cause);
    }

    public DeviceNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getReasonCode() {
        return REASON_CODE;
    }
}
