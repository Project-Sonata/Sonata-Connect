package testing.asserts;

import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import org.assertj.core.api.AbstractAssert;

import static java.lang.String.format;

public class DevicesAssert extends AbstractAssert<DevicesAssert, Devices> {

    protected DevicesAssert(Devices devices, Class<?> selfType) {
        super(devices, selfType);
    }

    public DevicesAssert(Devices devices) {
        super(devices, DevicesAssert.class);
    }

    public static DevicesAssert forDevices(Devices actual) {
        return new DevicesAssert(actual);
    }

    public DevicesAssert length(int requiredLength) {
        if (actual.size() != requiredLength) {
            failWithActualExpectedAndMessage(actual.size(), requiredLength, "Expected length to be equal");
        }
        return this;
    }

    public DevicesAssert empty() {
        if (!actual.isEmpty()) {
            failWithMessage("Devices must be empty!");
        }
        return this;
    }

    public DevicesAssert notEmpty() {
        if (actual.isEmpty()) {
            failWithMessage("Devices must be not empty!");
        }
        return this;
    }

    public DeviceAssert peekById(String id) {
        Device foundDevice = actual.stream().filter(device -> device.getDeviceId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format("Expected device with ID: %s but nothing was found", id)));

        return DeviceAssert.forDevice(foundDevice);
    }

    public DeviceAssert peekFirst() {
        return peek(0);
    }

    public DeviceAssert peekSecond() {
        return peek(1);
    }

    public DeviceAssert peekThird() {
        return peek(2);
    }

    public DeviceAssert peek(int index) {
        if (actual.size() <= index) {
            failWithMessage("The devices length is: %s, but the index was: %s", actual.size(), index);
        }
        Device actual = this.actual.get(index);
        return new DeviceAssert(actual);
    }
}
