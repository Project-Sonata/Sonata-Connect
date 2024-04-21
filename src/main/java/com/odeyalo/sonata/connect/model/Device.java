package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Represent a device of the user where a playback can be started
 * or from which playback state can be changed.
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class Device {
    @NotNull
    String deviceId;
    @NotNull
    String deviceName;
    @NotNull
    DeviceType deviceType;
    int volume;
    boolean active;
}
