package testing.asserts;

import com.odeyalo.sonata.connect.model.RepeatState;
import org.assertj.core.api.AbstractAssert;

public class RepeatStateAssert extends AbstractAssert<RepeatStateAssert, RepeatState> {

    protected RepeatStateAssert(RepeatState actual) {
        super(actual, RepeatStateAssert.class);
    }

    public RepeatStateAssert off() {
        return repeatStateAssert(RepeatState.OFF);
    }

    public RepeatStateAssert track() {
        return repeatStateAssert(RepeatState.TRACK);
    }

    public RepeatStateAssert context() {
        return repeatStateAssert(RepeatState.CONTEXT);
    }

    private RepeatStateAssert repeatStateAssert(RepeatState expected) {
        if (actual != expected) {
            failWithActualExpectedAndMessage(actual, expected, "Repeat state must be '%s'", expected);
        }
        return this;
    }
}
