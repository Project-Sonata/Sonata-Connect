package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class Device {
    String deviceId;
    String deviceName;
    DeviceType deviceType;
    int volume;
    boolean active;
}
