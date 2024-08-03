package com.odeyalo.sonata.connect.support.time;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public final class JavaClock implements Clock {
    private static final JavaClock INSTANCE = new JavaClock();

    private JavaClock() {}

    @NotNull
    public static JavaClock instance() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public Instant now() {
        return Instant.now();
    }

    @Override
    public long currentTimeMillis() {
        return now().toEpochMilli();
    }
}
