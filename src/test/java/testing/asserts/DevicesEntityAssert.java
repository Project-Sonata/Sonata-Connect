package testing.asserts;

import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.Devices;
import org.assertj.core.api.AbstractAssert;
import org.springframework.util.Assert;

public class DevicesEntityAssert extends AbstractAssert<DevicesEntityAssert, Devices> {

    protected DevicesEntityAssert(Devices actual) {
        super(actual, DevicesEntityAssert.class);
    }

    public static DevicesEntityAssert forDevices(Devices actual) {
        Assert.notNull(actual, "The actual must be not null!");
        return new DevicesEntityAssert(actual);
    }

    public DevicesEntityAssert length(int requiredLength) {
        if (actual.size() != requiredLength) {
            failWithActualExpectedAndMessage(actual.size(), requiredLength, "Expected length to be equal");
        }
        return this;
    }

    public DevicesEntityAssert empty() {
        if (!actual.isEmpty()) {
            failWithMessage("Devices must be empty!");
        }
        return this;
    }

    public DevicesEntityAssert notEmpty() {
        if (actual.isEmpty()) {
            failWithMessage("Devices must be not empty!");
        }
        return this;
    }

    public DeviceEntityAssert peekFirst() {
        return peek(0);
    }

    public DeviceEntityAssert peekSecond() {
        return peek(1);
    }

    public DeviceEntityAssert peekThird() {
        return peek(2);
    }

    public DeviceEntityAssert peek(int index) {
        if (actual.size() <= index) {
            failWithMessage("The devices length is: %s, but the index was: %s", actual.size(), index);
        }
        Device actual = this.actual.getDevice(index);
        return new DeviceEntityAssert(actual);
    }
}