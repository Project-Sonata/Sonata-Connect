package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryDeviceEntity implements DeviceEntity {
    String id;
    String name;
    DeviceType deviceType;
    int volume;
    boolean active;

    public static InMemoryDeviceEntity copy(DeviceEntity deviceEntity) {
        return new InMemoryDeviceEntity(deviceEntity.getId(), deviceEntity.getName(), deviceEntity.getDeviceType(), deviceEntity.getVolume(), deviceEntity.isActive());
    }
}
