package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Faker to create close-to-real data for {@link ConnectDeviceRequest}
 */
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectDeviceRequestFaker {
    String deviceId;
    String deviceName;
    DeviceType deviceType;
    int volume;

    final Faker faker = Faker.instance();

    protected ConnectDeviceRequestFaker() {
        this.deviceId = RandomStringUtils.randomAlphanumeric(16);
        this.deviceName = RandomStringUtils.randomAlphabetic(16);
        this.deviceType = faker.options().option(DeviceType.class);
        this.volume = faker.random().nextInt(0, 100);
    }

    public static ConnectDeviceRequestFaker create() {
        return new ConnectDeviceRequestFaker();
    }

    public ConnectDeviceRequest get() {
        return buildInMemoryDevice();
    }


    private ConnectDeviceRequest buildInMemoryDevice() {
        return ConnectDeviceRequest
                .builder()
                .id(deviceId)
                .name(deviceName)
                .deviceType(deviceType)
                .volume((byte) volume)
                .build();
    }
}
