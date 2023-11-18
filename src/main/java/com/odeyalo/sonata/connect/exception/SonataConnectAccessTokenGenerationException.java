package com.odeyalo.sonata.connect.exception;

/**
 * Thrown when access token in Sonata-Connect flow cannot be generated
 */
public class SonataConnectAccessTokenGenerationException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Error during generating access token!";

    public static SonataConnectAccessTokenGenerationException defaultException() {
        return new SonataConnectAccessTokenGenerationException(DEFAULT_MESSAGE);
    }

    public static SonataConnectAccessTokenGenerationException withCustomMessage(String message, Object... args) {
        return new SonataConnectAccessTokenGenerationException(message, args);
    }

    public static SonataConnectAccessTokenGenerationException withMessageAndCause(String message, Throwable cause) {
        return new SonataConnectAccessTokenGenerationException(message, cause);
    }

    protected SonataConnectAccessTokenGenerationException() {
        super();
    }

    protected SonataConnectAccessTokenGenerationException(String message, Object... args) {
        super(String.format(message, args));
    }

    protected SonataConnectAccessTokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    protected SonataConnectAccessTokenGenerationException(Throwable cause) {
        super(cause);
    }
}
