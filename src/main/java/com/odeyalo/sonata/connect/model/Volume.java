package com.odeyalo.sonata.connect.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

/**
 * Represent a volume for the device
 * Volume MUST BE in range from 0 to 100
 *
 * @param value - an integer that represent a volume
 */
public record Volume(int value) {
    /**
     * @throws IllegalStateException if a volume is in invalid range
     */
    public Volume {
        Assert.state(value >= 0, "Volume cannot be negative!");
        Assert.state(value <= 100, "Volume must be in range 0 - 100!");
    }

    @NotNull
    public static Volume from(final int value) {
        return new Volume(value);
    }

    @NotNull
    public static Volume fromInt(final int value) {
        return from(value);
    }

    @NotNull
    public static Volume muted() {
        return new Volume(0);
    }

    public int asInt() {
        return value;
    }
}
