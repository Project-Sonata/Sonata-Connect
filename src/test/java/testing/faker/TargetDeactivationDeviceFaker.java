package testing.faker;

import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevice;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Faker for {@link TargetDeactivationDevice}
 */
@Setter
public class TargetDeactivationDeviceFaker {
    String deviceId;

    protected TargetDeactivationDeviceFaker() {
        this.deviceId = RandomStringUtils.randomAlphanumeric(16);
    }

    public static TargetDeactivationDeviceFaker create() {
        return new TargetDeactivationDeviceFaker();
    }

    public TargetDeactivationDevice get() {
        return TargetDeactivationDevice.of(deviceId);
    }
}