package com.odeyalo.sonata.connect.exception;

public final class InvalidVolumeException extends RuntimeException {

    public static InvalidVolumeException withCustomMessage(final String message) {
        return new InvalidVolumeException(message);
    }

    public static InvalidVolumeException withMessageAndCause(final String message, final Throwable cause) {
        return new InvalidVolumeException(message, cause);
    }

    public InvalidVolumeException() {
        super();
    }

    public InvalidVolumeException(final String message) {
        super(message);
    }

    public InvalidVolumeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidVolumeException(final Throwable cause) {
        super(cause);
    }
}
