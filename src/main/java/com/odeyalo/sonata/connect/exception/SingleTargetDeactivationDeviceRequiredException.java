package com.odeyalo.sonata.connect.exception;

/**
 * Exception to throw when only single deactivation device is supported
 */
public class SingleTargetDeactivationDeviceRequiredException extends IllegalArgumentException {
    public static final String DEFAULT_MESSAGE = "Single deactivation required but was received more or less!";

    public static SingleTargetDeactivationDeviceRequiredException defaultException() {
        return new SingleTargetDeactivationDeviceRequiredException(DEFAULT_MESSAGE);
    }

    public static SingleTargetDeactivationDeviceRequiredException withCustomMessage(String message) {
        return new SingleTargetDeactivationDeviceRequiredException(message);
    }

    public static SingleTargetDeactivationDeviceRequiredException withMessageAndCause(String message, Throwable cause) {
        return new SingleTargetDeactivationDeviceRequiredException(message, cause);
    }

    public SingleTargetDeactivationDeviceRequiredException() {
        super();
    }

    public SingleTargetDeactivationDeviceRequiredException(String message) {
        super(message);
    }

    public SingleTargetDeactivationDeviceRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public SingleTargetDeactivationDeviceRequiredException(Throwable cause) {
        super(cause);
    }
}
