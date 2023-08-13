package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.Devices;
import com.odeyalo.sonata.connect.entity.InMemoryDevices;

import java.util.ArrayList;
import java.util.List;

public class DevicesFaker {
    List<Device> devices = new ArrayList<>();

    Faker faker = Faker.instance();

    public DevicesFaker() {
        int numberOfDevices = faker.random().nextInt(1, 7);
        for (int i = 0; i < numberOfDevices; i++) {
            Device device = DeviceFaker.create().get();
            this.devices.add(device);
        }
    }

    public DevicesFaker(int numberOfDevices) {
        for (int i = 0; i < numberOfDevices; i++) {
            Device device = DeviceFaker.create().get();
            this.devices.add(device);
        }
    }

    public static DevicesFaker create() {
        return new DevicesFaker();
    }
    public static DevicesFaker create(int numberOfDevices) {
        return new DevicesFaker(numberOfDevices);
    }

    public Devices get() {
        return buildInMemoryDevices();
    }

    public InMemoryDevices asInMemoryDevices() {
        return buildInMemoryDevices();
    }

    private InMemoryDevices buildInMemoryDevices() {
        return InMemoryDevices.builder()
                .devices(devices)
                .build();
    }
}
