package testing.faker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.InMemoryDevice;
import com.odeyalo.sonata.connect.entity.InMemoryPlayerState;
import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;

@Setter
@Accessors(chain = true)
public class DeviceFaker {
    String deviceId;
    String deviceName;
    DeviceType deviceType;
    int volume;
    boolean active;


    static final Faker faker = Faker.instance();

    protected DeviceFaker() {
        this(faker.random().nextBoolean());
    }

    protected DeviceFaker(boolean active) {
        this.deviceId = RandomStringUtils.randomAlphanumeric(10);
        this.deviceName = RandomStringUtils.randomAlphabetic(15);
        this.deviceType = faker.options().option(DeviceType.class);
        this.volume = faker.random().nextInt(0, 100);
        this.active = active;
    }

    public static DeviceFaker create() {
        return new DeviceFaker();
    }

    public static DeviceFaker createActiveDevice() {
        return new DeviceFaker(true);
    }
    public static DeviceFaker createInactiveDevice() {
        return new DeviceFaker(false);
    }

    public Device get() {
        return buildInMemoryDevice();
    }

    public Device asInMemoryDevice() {
        return buildInMemoryDevice();
    }

    private InMemoryDevice buildInMemoryDevice() {
        return InMemoryDevice
                .builder()
                .id(deviceId)
                .name(deviceName)
                .deviceType(deviceType)
                .volume(volume)
                .active(active)
                .build();
    }
}
