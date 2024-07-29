package com.odeyalo.sonata.connect.exception.web;

public final class MissingRequestParameterException extends RuntimeException {

    public MissingRequestParameterException(final String message) {
        super(message);
    }

    public MissingRequestParameterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
