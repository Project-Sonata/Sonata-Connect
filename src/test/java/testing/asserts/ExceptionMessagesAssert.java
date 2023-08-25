package testing.asserts;

import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import com.odeyalo.sonata.connect.dto.ExceptionMessages;
import org.assertj.core.api.AbstractAssert;

public class ExceptionMessagesAssert extends AbstractAssert<ExceptionMessagesAssert, ExceptionMessages> {
    protected ExceptionMessagesAssert(ExceptionMessages actual) {
        super(actual, ExceptionMessagesAssert.class);
    }

    public static ExceptionMessagesAssert forMessages(ExceptionMessages actual) {
        return new ExceptionMessagesAssert(actual);
    }

    public ExceptionMessagesAssert length(int expectedLength) {
        if (actual.getSize() != expectedLength) {
            failWithActualExpectedAndMessage(actual.getSize(), expectedLength,"Expected length to be equal!");
        }
        return this;
    }

    public ExceptionMessageAssert peekFirst() {
        return peek(0);
    }

    public ExceptionMessageAssert peekSecond() {
        return peek(1);
    }

    protected ExceptionMessageAssert peek(int index) {
        if (index >= actual.getSize()) {
            failWithMessage("Index [%s] is greater than size [%s]!", index, actual.getSize());
        }
        ExceptionMessage target = actual.getMessages().get(index);
        return ExceptionMessageAssert.forMessage(target);
    }
}