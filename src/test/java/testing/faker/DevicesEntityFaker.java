package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.InMemoryDevicesEntity;

import java.util.ArrayList;
import java.util.List;

public class DevicesEntityFaker {
    List<DeviceEntity> deviceEntities = new ArrayList<>();

    Faker faker = Faker.instance();
    boolean containActiveDevice = false;

    public DevicesEntityFaker() {
        int numberOfDevices = faker.random().nextInt(1, 7);
        fulfillDevices(numberOfDevices);
    }

    public DevicesEntityFaker(int numberOfDevices) {
        fulfillDevices(numberOfDevices);
    }

    public static DevicesEntityFaker create() {
        return new DevicesEntityFaker();
    }

    public static DevicesEntityFaker create(int numberOfDevices) {
        return new DevicesEntityFaker(numberOfDevices);
    }

    public DevicesEntity get() {
        return buildInMemoryDevices();
    }

    public InMemoryDevicesEntity asInMemoryDevices() {
        return buildInMemoryDevices();
    }

    private InMemoryDevicesEntity buildInMemoryDevices() {
        return InMemoryDevicesEntity.builder()
                .devices(deviceEntities)
                .build();
    }

    private void fulfillDevices(int numberOfDevices) {
        for (int i = 0; i < numberOfDevices; i++) {
            DeviceEntity deviceEntity = getDevice();
            this.deviceEntities.add(deviceEntity);
        }
    }

    private DeviceEntity getDevice() {
        DeviceEntity deviceEntity;
        if (containActiveDevice) {
            deviceEntity = DeviceEntityFaker.createInactiveDevice().asInMemoryDevice();
        } else {
            deviceEntity = DeviceEntityFaker.createActiveDevice().asInMemoryDevice();
            this.containActiveDevice = true;
        }
        return deviceEntity;
    }
}
