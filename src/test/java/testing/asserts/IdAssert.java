package testing.asserts;

import org.assertj.core.api.AbstractAssert;

public class IdAssert extends AbstractAssert<IdAssert, String> {

    protected IdAssert(String actual) {
        super(actual, IdAssert.class);
    }
}
