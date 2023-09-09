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
public class InMemoryDevicesEntity implements DevicesEntity {
    List<DeviceEntity> deviceEntities;

    public static InMemoryDevicesEntity empty() {
        return new InMemoryDevicesEntity(new ArrayList<>());
    }

    @Override
    public List<DeviceEntity> getDevices() {
        return deviceEntities;
    }

    public static class InMemoryDevicesEntityBuilder {
        private ArrayList<DeviceEntity> deviceEntities;

        InMemoryDevicesEntityBuilder() {
        }

        public InMemoryDevicesEntityBuilder device(DeviceEntity deviceEntity) {
            if (this.deviceEntities == null) this.deviceEntities = new ArrayList<DeviceEntity>();
            this.deviceEntities.add(deviceEntity);
            return this;
        }

        public InMemoryDevicesEntityBuilder devices(Collection<? extends DeviceEntity> devices) {
            if (this.deviceEntities == null) this.deviceEntities = new ArrayList<DeviceEntity>();
            this.deviceEntities.addAll(devices);
            return this;
        }

        public InMemoryDevicesEntityBuilder clearDevices() {
            if (this.deviceEntities != null)
                this.deviceEntities.clear();
            return this;
        }

        public InMemoryDevicesEntity build() {
            return new InMemoryDevicesEntity(deviceEntities);
        }
    }

    public List<DeviceEntity> getDeviceEntities() {
        return deviceEntities;
    }
}
