package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.stream.Stream;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class DevicesModel {
    List<DeviceModel> devices;

    public boolean isEmpty() {
        return devices.isEmpty();
    }

    public int size() {
        return devices.size();
    }

    public DeviceModel get(int index) {
        return devices.get(index);
    }

    public void addDevice(DeviceModel device) {
        this.devices.add(device);
    }

    public Stream<DeviceModel> stream() {
        return devices.stream();
    }
}
