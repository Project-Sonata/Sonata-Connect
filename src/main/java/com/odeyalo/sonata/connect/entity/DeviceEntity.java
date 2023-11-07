package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceEntity {
    String id;
    String name;
    DeviceType deviceType;
    int volume;
    boolean active;

    public static DeviceEntity copy(DeviceEntity deviceEntity) {
        return deviceEntity.toBuilder().build();
    }
}
