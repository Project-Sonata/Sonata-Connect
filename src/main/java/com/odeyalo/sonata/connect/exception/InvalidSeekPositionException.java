package com.odeyalo.sonata.connect.exception;

public final class InvalidSeekPositionException extends PlayerCommandException {
    public static final String REASON_CODE = "invalid_position";
    public static final String DESCRIPTION = "Invalid seek position supplied";

    private InvalidSeekPositionException() {
        super(DESCRIPTION, REASON_CODE);
    }

    private InvalidSeekPositionException(final String message) {
        super(message, REASON_CODE);
    }

    private InvalidSeekPositionException(final String message, final Throwable cause) {
        super(message, REASON_CODE, cause);
    }

    public static InvalidSeekPositionException defaultException() {
        return new InvalidSeekPositionException();
    }

    public static InvalidSeekPositionException withMessage(String message) {
        return new InvalidSeekPositionException(message);
    }
}
