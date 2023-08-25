package testing.asserts;

import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import org.assertj.core.api.AbstractAssert;

public class ExceptionMessageAssert extends AbstractAssert<ExceptionMessageAssert, ExceptionMessage> {
    protected ExceptionMessageAssert(ExceptionMessage actual) {
        super(actual, ExceptionMessageAssert.class);
    }

    public static ExceptionMessageAssert forMessage(ExceptionMessage actual) {
        return new ExceptionMessageAssert(actual);
    }


    public ExceptionMessageAssert isDescriptionEqualTo(String expectedDescription) {
        if (!actual.getDescription().equals(expectedDescription)) {
            failWithActualExpectedAndMessage(actual.getDescription(), expectedDescription,"Expected description to be equal!");
        }
        return this;
    }
}