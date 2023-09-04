package testing.asserts;

import com.odeyalo.sonata.common.context.ContextEntityType;
import com.odeyalo.sonata.common.context.ContextUri;
import org.assertj.core.api.AbstractAssert;

/**
 * Asserts for ContextUri
 */
public class ContextUriAssert extends AbstractAssert<ContextUriAssert, ContextUri> {
    protected ContextUriAssert(ContextUri actual, Class<?> self) {
        super(actual, self);
    }
    public ContextUriAssert(ContextUri actual) {
        super(actual, ContextUriAssert.class);
    }

    public ContextUriAssert entityType(ContextEntityType expectedType) {
        if (expectedType != actual.getType()) {
            failWithActualExpectedAndMessage(actual.getType(), expectedType, "Expected entity type to be equal!");
        }
        return this;
    }

    public ContextUriAssert entityId(String expectedId) {
        if (!expectedId.equals(actual.getEntityId())) {
            failWithActualExpectedAndMessage(actual.getEntityId(), expectedId, "Expected entity ID to be equal!");
        }
        return this;
    }
}
