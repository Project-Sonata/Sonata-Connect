package com.odeyalo.sonata.connect.exception;

/**
 * An exception that occurred when player state is not satisfied for executing the command.
 */
public final class IllegalCommandStateException extends PlayerCommandException {
    static final String REASON_CODE = "invalid_command_state";
    static final String MESSAGE = "Player command failed: Player state is not ready to execute this code";

    public IllegalCommandStateException(final String message) {
        super(message, REASON_CODE);
    }

    public static IllegalCommandStateException defaultException() {
        return new IllegalCommandStateException(MESSAGE);
    }

    public static IllegalCommandStateException withCustomMessage(String message) {
        return new IllegalCommandStateException(message);
    }
}
