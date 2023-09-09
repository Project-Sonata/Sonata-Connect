package com.odeyalo.sonata.connect.repository.storage;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DevicesEntity that does not depend on any database implementation
 */
@Data
@AllArgsConstructor(staticName = "of")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersistableDevicesEntity implements DevicesEntity {
    @Getter(value = AccessLevel.NONE)
    List<DeviceEntity> deviceEntities;

    public static PersistableDevicesEntity empty() {
        return new PersistableDevicesEntity(new ArrayList<>());
    }

    public static PersistableDevicesEntity copyFrom(DevicesEntity copyTarget) {
        return new PersistableDevicesEntity(new ArrayList<>(copyTarget.getDevices()));
    }

    @Override
    public List<DeviceEntity> getDevices() {
        return deviceEntities;
    }

    public static class PersistableDevicesEntityBuilder {
        private ArrayList<DeviceEntity> deviceEntities = new ArrayList<>();

        public PersistableDevicesEntityBuilder device(DeviceEntity deviceEntity) {
            this.deviceEntities.add(deviceEntity);
            return this;
        }

        public PersistableDevicesEntityBuilder devices(Collection<? extends DeviceEntity> devices) {
            this.deviceEntities.addAll(devices);
            return this;
        }

        public PersistableDevicesEntityBuilder clearDevices() {
            return this;
        }

        public PersistableDevicesEntity build() {
            return new PersistableDevicesEntity(deviceEntities);
        }
    }
}
