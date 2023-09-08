package com.odeyalo.sonata.connect.exception;

/**
 * Extension for IllegalStateException that should never happen
 */
public class NeverHappeningException extends IllegalStateException {
    public static final String DEFAULT_MESSAGE = "This exception should never be occurred!";

    public static NeverHappeningException defaultException() {
        return new NeverHappeningException(DEFAULT_MESSAGE);
    }

    public static NeverHappeningException withCustomMessage(String message) {
        return new NeverHappeningException(message);
    }

    public static NeverHappeningException withMessageAndCause(String message, Throwable cause) {
        return new NeverHappeningException(message, cause);
    }

    public NeverHappeningException() {
        super();
    }

    public NeverHappeningException(String message) {
        super(message);
    }

    public NeverHappeningException(String message, Throwable cause) {
        super(message, cause);
    }

    public NeverHappeningException(Throwable cause) {
        super(cause);
    }
}
