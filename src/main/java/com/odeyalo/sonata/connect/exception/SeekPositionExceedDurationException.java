package com.odeyalo.sonata.connect.exception;

import org.jetbrains.annotations.NotNull;

public final class SeekPositionExceedDurationException extends PlayerCommandException {
    public static final String REASON_CODE = "seek_position_exceed";

    public SeekPositionExceedDurationException(@NotNull final String message) {
        super(message, REASON_CODE);
    }

    public SeekPositionExceedDurationException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, REASON_CODE, cause);
    }
}
