package testing.asserts;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.model.DeviceType;
import org.apache.commons.lang.BooleanUtils;
import org.assertj.core.api.AbstractAssert;
import org.springframework.util.Assert;

import static org.apache.commons.lang.BooleanUtils.isFalse;

public class DeviceDtoAssert extends AbstractAssert<DeviceDtoAssert, DeviceDto> {

    public DeviceDtoAssert(DeviceDto actual) {
        super(actual, DeviceDtoAssert.class);
    }

    public static DeviceDtoAssert forDevice(DeviceDto actual) {
        Assert.notNull(actual, "Actual must be not null");
        return new DeviceDtoAssert(actual);
    }

    public DeviceDtoAssert id(String expectedId) {
        if (!expectedId.equals(actual.getDeviceId())) {
            failWithActualExpectedAndMessage(actual.getDeviceId(), expectedId, "Expected devices ID to be equal");
        }
        return this;
    }

    public DeviceDtoAssert name(String expectedName) {
        if (!expectedName.equals(actual.getDeviceName())) {
            failWithActualExpectedAndMessage(actual.getDeviceName(), expectedName, "Expected device names to be equal");
        }
        return this;
    }

    public DeviceDtoAssert type(DeviceType expectedType) {
        if (expectedType != actual.getDeviceType()) {
            failWithActualExpectedAndMessage(actual.getDeviceType(), expectedType, "Expected device types to be equal");
        }
        return this;
    }

    public DeviceDtoAssert volume(int expectedVolume) {
        if (expectedVolume != actual.getVolume()) {
            failWithActualExpectedAndMessage(actual.getVolume(), expectedVolume, "Expected device volumes to be equal");
        }
        return this;
    }

    public DeviceDtoAssert active() {
        if (isFalse(actual.isActive())) {
            failWithMessage("Expected device to be in 'inactive' state!");
        }
        return this;
    }

    public DeviceDtoAssert inactive() {
        if (actual.isActive()) {
            failWithMessage("Expected device to be in 'active' state!");
        }
        return this;
    }
}
