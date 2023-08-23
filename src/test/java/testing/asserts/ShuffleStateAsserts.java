package testing.asserts;

import org.assertj.core.api.AbstractAssert;

public  class ShuffleStateAsserts extends AbstractAssert<ShuffleStateAsserts, Boolean> {
    public static final boolean ON = true;
    public static final boolean OFF = false;

    public ShuffleStateAsserts(Boolean actual) {
        super(actual, ShuffleStateAsserts.class);
    }

    public ShuffleStateAsserts on() {
        return shuffleStateAssert(ON);
    }

    public ShuffleStateAsserts off() {
        return shuffleStateAssert(OFF);
    }

    private ShuffleStateAsserts shuffleStateAssert(boolean expected) {
        if (actual != expected) {
            failWithActualExpectedAndMessage(actual, expected, "The player state should be: %s(%s)", expected ? "ON" : "OFF", expected);
        }
        return this;
    }
}
