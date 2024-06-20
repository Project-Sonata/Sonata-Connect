package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.DeviceSpec;
import com.odeyalo.sonata.connect.model.DeviceType;
import org.apache.commons.lang3.RandomStringUtils;

public final class DeviceFaker {
    private final Device.DeviceBuilder builder = Device.builder();
    private final Faker faker = Faker.instance();

    public DeviceFaker() {
        builder
                .deviceId(RandomStringUtils.randomAlphanumeric(16))
                .deviceName(faker.funnyName().name())
                .volume(DeviceSpec.Volume.from(40))
                .deviceType(faker.options().option(DeviceType.class));
    }

    public static DeviceFaker create() {
        return new DeviceFaker();
    }

    public Device get() {
        return builder.build();
    }
}
