package testing.asserts;

import com.odeyalo.sonata.connect.dto.AvailableDevicesResponseDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import org.assertj.core.api.AbstractAssert;
import org.jetbrains.annotations.NotNull;

public class AvailableDevicesResponseDtoAssert extends AbstractAssert<AvailableDevicesResponseDtoAssert, AvailableDevicesResponseDto> {

    public AvailableDevicesResponseDtoAssert(AvailableDevicesResponseDto actual) {
        super(actual, AvailableDevicesResponseDtoAssert.class);
    }

    public static AvailableDevicesResponseDtoAssert forBody(AvailableDevicesResponseDto actual) {
        return new AvailableDevicesResponseDtoAssert(actual);
    }

    public AvailableDevicesResponseDtoAssert length(int expectedLength) {
        if (expectedLength != actual.getSize()) {
            failWithActualExpectedAndMessage(actual.getSize(), expectedLength, "Expected sizes to be equal");
        }
        return this;
    }

    public DevicesDtoAssert devices() {
        return new DevicesDtoAssert(actual.getDevices());
    }
}
