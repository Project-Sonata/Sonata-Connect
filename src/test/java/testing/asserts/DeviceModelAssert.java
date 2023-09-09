package testing.asserts;

import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.DeviceType;
import org.assertj.core.api.AbstractAssert;

import static org.apache.commons.lang.BooleanUtils.isFalse;

public class DeviceModelAssert extends AbstractAssert<DeviceModelAssert, DeviceModel> {

    public DeviceModelAssert(DeviceModel actual) {
        super(actual, DeviceModelAssert.class);
    }

    public static DeviceModelAssert forDevice(DeviceModel actual) {
        return new DeviceModelAssert(actual);
    }

    public DeviceModelAssert id(String expectedId) {
        if (!expectedId.equals(actual.getDeviceId())) {
            failWithActualExpectedAndMessage(actual.getDeviceId(), expectedId, "Expected devices ID to be equal");
        }
        return this;
    }

    public DeviceModelAssert name(String expectedName) {
        if (!expectedName.equals(actual.getDeviceName())) {
            failWithActualExpectedAndMessage(actual.getDeviceName(), expectedName, "Expected device names to be equal");
        }
        return this;
    }

    public DeviceModelAssert type(DeviceType expectedType) {
        if (expectedType != actual.getDeviceType()) {
            failWithActualExpectedAndMessage(actual.getDeviceType(), expectedType, "Expected device types to be equal");
        }
        return this;
    }

    public DeviceModelAssert volume(int expectedVolume) {
        if (expectedVolume != actual.getVolume()) {
            failWithActualExpectedAndMessage(actual.getVolume(), expectedVolume, "Expected device volumes to be equal");
        }
        return this;
    }

    public DeviceModelAssert active() {
        if (isFalse(actual.isActive())) {
            failWithMessage("Expected device to be in 'active' state!");
        }
        return this;
    }

    public DeviceModelAssert inactive() {
        if (actual.isActive()) {
            failWithMessage("Expected device to be in 'inactive' state!");
        }
        return this;
    }
}
