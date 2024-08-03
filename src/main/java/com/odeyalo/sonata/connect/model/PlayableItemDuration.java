package com.odeyalo.sonata.connect.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * Represent a duration of the {@link PlayableItem}
 */
@Value
public class PlayableItemDuration {
    @Getter(value = AccessLevel.PRIVATE)
    long durationMs;

    public PlayableItemDuration(long durationMs) {
        this.durationMs = durationMs;
    }

    public static PlayableItemDuration ofMilliseconds(long durationMs) {
        Assert.state(durationMs >= 0, "A duration cannot be negative!");
        return new PlayableItemDuration(durationMs);
    }

    @NotNull
    public static PlayableItemDuration ofSeconds(long durationSec) {
        return ofMilliseconds(durationSec * 1000);
    }

    @NotNull
    public static PlayableItemDuration fromJavaDuration(@NotNull final Duration itemDuration) {
        return ofMilliseconds(itemDuration.toMillis());
    }

    public long asMilliseconds() {
        return durationMs;
    }

    public long asSeconds() {
        return durationMs / 1000;
    }
}
