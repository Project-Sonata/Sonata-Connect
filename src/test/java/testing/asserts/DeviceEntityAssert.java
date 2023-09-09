package testing.asserts;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.model.DeviceType;
import org.assertj.core.api.AbstractAssert;
import org.springframework.util.Assert;

import static org.apache.commons.lang.BooleanUtils.isFalse;

public class DeviceEntityAssert extends AbstractAssert<DeviceEntityAssert, DeviceEntity> {

    public DeviceEntityAssert(DeviceEntity actual) {
        super(actual, DeviceEntityAssert.class);
    }

    public static DeviceEntityAssert forDevice(DeviceEntity actual) {
        Assert.notNull(actual, "Actual must be not null");
        return new DeviceEntityAssert(actual);
    }

    public DeviceEntityAssert id(String expectedId) {
        if (!expectedId.equals(actual.getId())) {
            failWithActualExpectedAndMessage(actual.getId(), expectedId, "Expected devices ID to be equal");
        }
        return this;
    }

    public DeviceEntityAssert name(String expectedName) {
        if (!expectedName.equals(actual.getName())) {
            failWithActualExpectedAndMessage(actual.getName(), expectedName, "Expected device names to be equal");
        }
        return this;
    }

    public DeviceEntityAssert type(DeviceType expectedType) {
        if (expectedType != actual.getDeviceType()) {
            failWithActualExpectedAndMessage(actual.getDeviceType(), expectedType, "Expected device types to be equal");
        }
        return this;
    }

    public DeviceEntityAssert volume(int expectedVolume) {
        if (expectedVolume != actual.getVolume()) {
            failWithActualExpectedAndMessage(actual.getVolume(), expectedVolume, "Expected device volumes to be equal");
        }
        return this;
    }

    public DeviceEntityAssert active() {
        if (isFalse(actual.isActive())) {
            failWithMessage("Expected device to be in 'inactive' state!");
        }
        return this;
    }

    public DeviceEntityAssert inactive() {
        if (actual.isActive()) {
            failWithMessage("Expected device to be in 'active' state!");
        }
        return this;
    }
}
