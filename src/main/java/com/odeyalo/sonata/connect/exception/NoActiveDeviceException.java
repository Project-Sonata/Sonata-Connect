package com.odeyalo.sonata.connect.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Exception that should be thrown when there is no active device found for the given user
 */
@Getter
public class NoActiveDeviceException extends PlayerCommandException {
    static final String REASON_CODE = "no_active_device";
    static final String DEFAULT_MESSAGE = "At least one connected device is required to execute this command";

    @NotNull
    public static NoActiveDeviceException defaultException() {
        return withCustomMessage(DEFAULT_MESSAGE);
    }

    @NotNull
    public static NoActiveDeviceException withCustomMessage(@NotNull final String message) {
        return new NoActiveDeviceException(message);
    }

    @NotNull
    public static NoActiveDeviceException withMessageAndCause(@NotNull final String message,
                                                              @NotNull final Throwable cause) {
        return new NoActiveDeviceException(message, cause);
    }

    public NoActiveDeviceException() {
        super(DEFAULT_MESSAGE, REASON_CODE);
    }

    public NoActiveDeviceException(@NotNull final String message) {
        super(message, REASON_CODE);
    }

    private NoActiveDeviceException(@NotNull final String message,
                                    @NotNull final Throwable cause) {
        super(message, REASON_CODE, cause);
    }
}