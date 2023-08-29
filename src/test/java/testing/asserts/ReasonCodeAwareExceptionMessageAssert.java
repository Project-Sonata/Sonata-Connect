package testing.asserts;

import com.odeyalo.sonata.connect.dto.ReasonCodeAwareExceptionMessage;
import org.assertj.core.api.AbstractAssert;

public class ReasonCodeAwareExceptionMessageAssert extends AbstractAssert<ReasonCodeAwareExceptionMessageAssert, ReasonCodeAwareExceptionMessage> {

    protected ReasonCodeAwareExceptionMessageAssert(ReasonCodeAwareExceptionMessage actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public ReasonCodeAwareExceptionMessageAssert(ReasonCodeAwareExceptionMessage actual) {
        super(actual, ReasonCodeAwareExceptionMessageAssert.class);
    }

    public static ReasonCodeAwareExceptionMessageAssert forMessage(ReasonCodeAwareExceptionMessage actual) {
        return new ReasonCodeAwareExceptionMessageAssert(actual);
    }

    public DescriptionAssert description() {
        return new DescriptionAssert(actual.getDescription());
    }

    public ReasonCodeAssert reasonCode() {
        return new ReasonCodeAssert(actual.getReasonCode());
    }

    public static class DescriptionAssert extends AbstractAssert<DescriptionAssert, String> {

        protected DescriptionAssert(String actual, Class<?> selfType) {
            super(actual, selfType);
        }

        public DescriptionAssert(String actual) {
            super(actual, DescriptionAssert.class);
        }
    }

    public static class ReasonCodeAssert extends AbstractAssert<ReasonCodeAssert, String> {

        protected ReasonCodeAssert(String actual, Class<?> selfType) {
            super(actual, selfType);
        }

        public ReasonCodeAssert(String actual) {
            super(actual, ReasonCodeAssert.class);
        }
    }
}
