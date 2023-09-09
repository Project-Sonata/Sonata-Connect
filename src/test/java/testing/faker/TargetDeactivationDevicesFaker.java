package testing.faker;

import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevice;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;

import java.util.ArrayList;
import java.util.List;

/**
 * Faker for {@link TargetDeactivationDevices}
 */
public class TargetDeactivationDevicesFaker {
    List<TargetDeactivationDevice> devices = new ArrayList<>();

    public TargetDeactivationDevicesFaker(int numberOfDevices) {
        for (int i = 0; i < numberOfDevices; i++) {
            devices.add(TargetDeactivationDeviceFaker.create().get());
        }
    }

    public static TargetDeactivationDevicesFaker create(int numberOfDevices) {
        return new TargetDeactivationDevicesFaker(numberOfDevices);
    }

    public TargetDeactivationDevices get() {
        return TargetDeactivationDevices.of(devices);
    }
}
