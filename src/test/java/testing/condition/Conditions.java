package testing.condition;

import com.odeyalo.sonata.connect.exception.ReasonCodeAware;
import org.assertj.core.api.Condition;

/**
 * Static factory to provide AssetJ conditions
 */
public class Conditions {

    public static Condition<Throwable> reasonCodeEqual(String expectedCode) {
        return new Condition<>(throwable -> ((ReasonCodeAware) throwable).getReasonCode().equals(expectedCode), "The code must be equal!");
    }
}
