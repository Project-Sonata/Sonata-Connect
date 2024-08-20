package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.exception.InvalidSeekPositionException;
import com.odeyalo.sonata.connect.model.PlayableItemDuration;
import org.jetbrains.annotations.NotNull;

public record SeekPosition(long posMs) {

    public SeekPosition {
        if ( posMs < 0 ) {
            throw InvalidSeekPositionException.defaultException();
        }
    }

    @NotNull
    public static SeekPosition ofMillis(long millis) {
        return new SeekPosition(millis);
    }

    @NotNull
    public static SeekPosition ofSeconds(int seconds) {
        return new SeekPosition(seconds * 1000L);
    }

    public boolean exceeds(@NotNull final PlayableItemDuration duration) {
        return duration.isExceeded(posMs);
    }
}
