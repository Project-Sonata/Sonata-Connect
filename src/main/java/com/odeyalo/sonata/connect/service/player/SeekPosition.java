package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.PlayableItem;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public record SeekPosition(int posMs) {

    public SeekPosition {
        Assert.state(posMs >= 0, "Position must be positive!");
    }

    @NotNull
    public static SeekPosition ofMillis(int millis) {
        return new SeekPosition(millis);
    }

    @NotNull
    public static SeekPosition ofSeconds(int seconds) {
        return new SeekPosition(seconds * 1000);
    }

    public boolean exceeds(@NotNull final PlayableItem item) {
        return item.getDuration().isExceeded(posMs);
    }
}
