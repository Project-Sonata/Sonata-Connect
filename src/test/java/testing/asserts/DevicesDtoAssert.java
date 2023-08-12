package testing.asserts;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import org.assertj.core.api.AbstractAssert;
import org.springframework.util.Assert;

public class DevicesDtoAssert extends AbstractAssert<DevicesDtoAssert, DevicesDto> {

    protected DevicesDtoAssert(DevicesDto actual) {
        super(actual, DevicesDtoAssert.class);
    }

    public static DevicesDtoAssert forDevices(DevicesDto actual) {
        Assert.notNull(actual, "The actual must be not null!");
        return new DevicesDtoAssert(actual);
    }

    public DevicesDtoAssert length(int requiredLength) {
        if (actual.size() != requiredLength) {
            failWithActualExpectedAndMessage(actual.size(), requiredLength, "Expected length to be equal");
        }
        return this;
    }

    public DevicesDtoAssert empty() {
        if (!actual.isEmpty()) {
            failWithMessage("Devices must be empty!");
        }
        return this;
    }

    public DevicesDtoAssert notEmpty() {
        if (actual.isEmpty()) {
            failWithMessage("Devices must be not empty!");
        }
        return this;
    }

    public DeviceDtoAssert peekFirst() {
        return peek(0);
    }

    public DeviceDtoAssert peekSecond() {
        return peek(1);
    }

    public DeviceDtoAssert peekThird() {
        return peek(2);
    }

    public DeviceDtoAssert peek(int index) {
        if (actual.size() <= index) {
            failWithMessage("The devices length is: %s, but the index was: %s", actual.size(), index);
        }
        DeviceDto actual = this.actual.get(index);
        return new DeviceDtoAssert(actual);
    }
}
