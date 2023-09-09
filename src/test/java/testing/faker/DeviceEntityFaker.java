package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.InMemoryDeviceEntity;
import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;

@Setter
@Accessors(chain = true)
public class DeviceEntityFaker {
    String deviceId;
    String deviceName;
    DeviceType deviceType;
    int volume;
    boolean active;


    static final Faker faker = Faker.instance();

    protected DeviceEntityFaker() {
        this(faker.random().nextBoolean());
    }

    protected DeviceEntityFaker(boolean active) {
        this.deviceId = RandomStringUtils.randomAlphanumeric(10);
        this.deviceName = RandomStringUtils.randomAlphabetic(15);
        this.deviceType = faker.options().option(DeviceType.class);
        this.volume = faker.random().nextInt(0, 100);
        this.active = active;
    }

    public static DeviceEntityFaker create() {
        return new DeviceEntityFaker();
    }

    public static DeviceEntityFaker createActiveDevice() {
        return new DeviceEntityFaker(true);
    }

    public static DeviceEntityFaker createInactiveDevice() {
        return new DeviceEntityFaker(false);
    }

    public DeviceEntity get() {
        return buildInMemoryDevice();
    }

    public DeviceEntity asInMemoryDevice() {
        return buildInMemoryDevice();
    }

    private InMemoryDeviceEntity buildInMemoryDevice() {
        return InMemoryDeviceEntity
                .builder()
                .id(deviceId)
                .name(deviceName)
                .deviceType(deviceType)
                .volume(volume)
                .active(active)
                .build();
    }
}
