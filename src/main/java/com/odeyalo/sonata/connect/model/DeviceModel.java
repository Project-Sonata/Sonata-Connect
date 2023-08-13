package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class DeviceModel {
    String deviceId;
    String deviceName;
    DeviceType deviceType;
    int volume;
    boolean active;
}
