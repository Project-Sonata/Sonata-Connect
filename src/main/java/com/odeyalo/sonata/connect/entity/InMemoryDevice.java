package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryDevice implements Device {
    String id;
    String name;
    DeviceType deviceType;
    int volume;
    boolean active;
}
