package com.odeyalo.sonata.connect.repository.storage;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DeviceEntity that does not depend on specific database.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersistableDeviceEntity implements DeviceEntity {
    String id;
    String name;
    DeviceType deviceType;
    int volume;
    boolean active;

    public static PersistableDeviceEntity copy(DeviceEntity deviceEntity) {
        return PersistableDeviceEntity.builder()
                .id(deviceEntity.getId())
                .name(deviceEntity.getName())
                .deviceType(deviceEntity.getDeviceType())
                .volume(deviceEntity.getVolume())
                .active(deviceEntity.isActive())
                .build();
    }
}