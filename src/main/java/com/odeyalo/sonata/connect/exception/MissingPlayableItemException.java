package com.odeyalo.sonata.connect.exception;

import org.jetbrains.annotations.NotNull;

public final class MissingPlayableItemException extends PlayerCommandException{
    public static final String REASON_CODE = "playable_item_required";

    public MissingPlayableItemException(@NotNull final String message) {
        super(message, REASON_CODE);
    }

    public MissingPlayableItemException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, REASON_CODE, cause);
    }
}
