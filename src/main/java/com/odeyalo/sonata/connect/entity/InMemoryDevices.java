package com.odeyalo.sonata.connect.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryDevices implements Devices {
    List<Device> devices;

    public static InMemoryDevices empty() {
        return new InMemoryDevices(new ArrayList<>());
    }

    public static class InMemoryDevicesBuilder {
        private ArrayList<Device> devices;

        InMemoryDevicesBuilder() {
        }

        public InMemoryDevicesBuilder device(Device device) {
            if (this.devices == null) this.devices = new ArrayList<Device>();
            this.devices.add(device);
            return this;
        }

        public InMemoryDevicesBuilder devices(Collection<? extends Device> devices) {
            if (this.devices == null) this.devices = new ArrayList<Device>();
            this.devices.addAll(devices);
            return this;
        }

        public InMemoryDevicesBuilder clearDevices() {
            if (this.devices != null)
                this.devices.clear();
            return this;
        }

        public InMemoryDevices build() {
            return new InMemoryDevices(devices);
        }
    }

    @Override
    public List<Device> getDevices() {
        return devices;
    }
}
