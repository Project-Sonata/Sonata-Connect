package com.odeyalo.sonata.connect.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryDevices implements Devices {
    @Singular
    List<Device> devices;

    public static InMemoryDevices empty() {
        return new InMemoryDevices(new ArrayList<>());
    }

    @Override
    public List<Device> getDevices() {
        return devices;
    }
}
