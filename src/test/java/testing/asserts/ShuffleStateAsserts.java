package testing.asserts;

import com.odeyalo.sonata.connect.model.ShuffleMode;
import org.assertj.core.api.AbstractAssert;

import static com.odeyalo.sonata.connect.model.ShuffleMode.ENABLED;
import static com.odeyalo.sonata.connect.model.ShuffleMode.OFF;

public  class ShuffleStateAsserts extends AbstractAssert<ShuffleStateAsserts, ShuffleMode> {

    public ShuffleStateAsserts(ShuffleMode actual) {
        super(actual, ShuffleStateAsserts.class);
    }

    public ShuffleStateAsserts on() {
        return shuffleStateAssert(ENABLED);
    }

    public ShuffleStateAsserts off() {
        return shuffleStateAssert(OFF);
    }

    private ShuffleStateAsserts shuffleStateAssert(ShuffleMode expected) {
        if (actual != expected) {
            failWithActualExpectedAndMessage(actual, expected, "The player state should be: %s", expected);
        }
        return this;
    }
}
