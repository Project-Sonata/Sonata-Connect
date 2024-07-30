package com.odeyalo.sonata.connect.exception;

public final class PlayableItemNotFoundException extends PlayerCommandException {
    static final String REASON_CODE = "playable_item_not_exist";

    public PlayableItemNotFoundException(final String message) {
        super(message, REASON_CODE);
    }

    public static PlayableItemNotFoundException defaultException() {
        return new PlayableItemNotFoundException("Could not load playable item");
    }

    public static PlayableItemNotFoundException withCustomMessage(String message) {
        return new PlayableItemNotFoundException(message);
    }
}
