package testing.time;

import com.odeyalo.sonata.connect.support.time.Clock;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * An implementation of {@link Clock} that exposes methods to control the time in tests
 */
public final class TestingClock implements Clock {
    private Instant value;

    public TestingClock(@NotNull final Instant initialValue) {
        this.value = initialValue;
    }

    @Override
    @NotNull
    public Instant now() {
        return value;
    }

    @Override
    public long currentTimeMillis() {
        return value.toEpochMilli();
    }

    /**
     * Simulate waiting for specified number of seconds
     * @param seconds - seconds to wait
     */
    public void waitSeconds(final int seconds) {
        this.value = value.plusSeconds(seconds);
    }

    /**
     * Same as {@link #waitSeconds(int)} but uses milliseconds
     * @param millis - millis to wait
     */
    public void waitMillis(final long millis) {
        this.value = value.plusMillis(millis);
    }
}
