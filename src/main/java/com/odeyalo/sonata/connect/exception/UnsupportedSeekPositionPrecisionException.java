package com.odeyalo.sonata.connect.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@EqualsAndHashCode(callSuper = true)
public class UnsupportedSeekPositionPrecisionException extends PlayerCommandException {

    private static final String REASON_CODE = "unsupported_precision";
    private static final String DEFAULT_DESCRIPTION = "Seek position precision of received type is not supported";

    private final String receivedPrecision;

    public UnsupportedSeekPositionPrecisionException(final String receivedPrecision) {
        super(DEFAULT_DESCRIPTION, REASON_CODE);
        this.receivedPrecision = receivedPrecision;
    }

    public UnsupportedSeekPositionPrecisionException(@NotNull final String message,
                                                     @NotNull final String receivedPrecision) {
        super(message, REASON_CODE);
        this.receivedPrecision = receivedPrecision;
    }

    public UnsupportedSeekPositionPrecisionException(final String message, final String receivedPrecision, final Throwable cause) {
        super(message, REASON_CODE, cause);
        this.receivedPrecision = receivedPrecision;
    }
}
