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
    boolean containActiveDevice = false;

    public DevicesFaker() {
        int numberOfDevices = faker.random().nextInt(1, 7);
        fulfillDevices(numberOfDevices);
    }

    public DevicesFaker(int numberOfDevices) {
        fulfillDevices(numberOfDevices);
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

    private void fulfillDevices(int numberOfDevices) {
        for (int i = 0; i < numberOfDevices; i++) {
            Device device = getDevice();
            this.devices.add(device);
        }
    }

    private Device getDevice() {
        Device device;
        if (containActiveDevice) {
            device = DeviceFaker.createInactiveDevice().asInMemoryDevice();
        } else {
            device = DeviceFaker.createActiveDevice().asInMemoryDevice();
            this.containActiveDevice = true;
        }
        return device;
    }
}
