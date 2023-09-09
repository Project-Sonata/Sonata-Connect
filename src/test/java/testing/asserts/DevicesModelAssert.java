package testing.asserts;

import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.DevicesModel;
import org.assertj.core.api.AbstractAssert;

import static java.lang.String.format;

public class DevicesModelAssert extends AbstractAssert<DevicesModelAssert, DevicesModel> {

    protected DevicesModelAssert(DevicesModel devicesModel, Class<?> selfType) {
        super(devicesModel, selfType);
    }

    public DevicesModelAssert(DevicesModel devicesModel) {
        super(devicesModel, DevicesModelAssert.class);
    }

    public static DevicesModelAssert forDevices(DevicesModel actual) {
        return new DevicesModelAssert(actual);
    }

    public DevicesModelAssert length(int requiredLength) {
        if (actual.size() != requiredLength) {
            failWithActualExpectedAndMessage(actual.size(), requiredLength, "Expected length to be equal");
        }
        return this;
    }

    public DevicesModelAssert empty() {
        if (!actual.isEmpty()) {
            failWithMessage("Devices must be empty!");
        }
        return this;
    }

    public DevicesModelAssert notEmpty() {
        if (actual.isEmpty()) {
            failWithMessage("Devices must be not empty!");
        }
        return this;
    }

    public DeviceModelAssert peekById(String id) {
        DeviceModel foundDevice = actual.getDevices().stream().filter(device -> device.getDeviceId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format("Expected device with ID: %s but nothing was found", id)));

        return DeviceModelAssert.forDevice(foundDevice);
    }

    public DeviceModelAssert peekFirst() {
        return peek(0);
    }

    public DeviceModelAssert peekSecond() {
        return peek(1);
    }

    public DeviceModelAssert peekThird() {
        return peek(2);
    }

    public DeviceModelAssert peek(int index) {
        if (actual.size() <= index) {
            failWithMessage("The devices length is: %s, but the index was: %s", actual.size(), index);
        }
        DeviceModel actual = this.actual.get(index);
        return new DeviceModelAssert(actual);
    }
}
