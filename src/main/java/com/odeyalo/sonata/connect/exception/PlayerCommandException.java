package com.odeyalo.sonata.connect.exception;

public class PlayerCommandException extends RuntimeException implements ReasonCodeAware {
    private final String reasonCode;

    public PlayerCommandException(final String message, final String reasonCode) {
        super(message);
        this.reasonCode = reasonCode;
    }

    public PlayerCommandException(final String message, final String reasonCode, final Throwable cause) {
        super(message, cause);
        this.reasonCode = reasonCode;
    }

    @Override
    public String getReasonCode() {
        return reasonCode;
    }
}
