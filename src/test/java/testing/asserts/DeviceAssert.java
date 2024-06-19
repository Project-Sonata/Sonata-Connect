package testing.asserts;

import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.DeviceType;
import org.assertj.core.api.AbstractAssert;

import static org.apache.commons.lang.BooleanUtils.isFalse;

public class DeviceAssert extends AbstractAssert<DeviceAssert, Device> {

    public DeviceAssert(Device actual) {
        super(actual, DeviceAssert.class);
    }

    public static DeviceAssert forDevice(Device actual) {
        return new DeviceAssert(actual);
    }

    public DeviceAssert id(String expectedId) {
        if (!expectedId.equals(actual.getDeviceId())) {
            failWithActualExpectedAndMessage(actual.getDeviceId(), expectedId, "Expected devices ID to be equal");
        }
        return this;
    }

    public DeviceAssert name(String expectedName) {
        if (!expectedName.equals(actual.getDeviceName())) {
            failWithActualExpectedAndMessage(actual.getDeviceName(), expectedName, "Expected device names to be equal");
        }
        return this;
    }

    public DeviceAssert type(DeviceType expectedType) {
        if (expectedType != actual.getDeviceType()) {
            failWithActualExpectedAndMessage(actual.getDeviceType(), expectedType, "Expected device types to be equal");
        }
        return this;
    }

    public DeviceAssert volume(int expectedVolume) {
        if (expectedVolume != actual.getVolume()) {
            failWithActualExpectedAndMessage(actual.getVolume(), expectedVolume, "Expected device volumes to be equal");
        }
        return this;
    }

    public DeviceAssert isActive() {
        if (isFalse(actual.isActive())) {
            failWithMessage("Expected device to be in 'active' state!");
        }
        return this;
    }

    public DeviceAssert isIdle() {
        if (actual.isActive()) {
            failWithMessage("Expected device to be in 'inactive' state!");
        }
        return this;
    }
}
