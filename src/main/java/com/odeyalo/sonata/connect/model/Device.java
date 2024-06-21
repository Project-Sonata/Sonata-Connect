package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.jetbrains.annotations.NotNull;

/**
 * Represent a device of the user where a playback can be started
 * or from which playback state can be changed.
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
@With
public class Device implements DeviceSpec {
    @NotNull
    String deviceId;
    @NotNull
    String deviceName;
    @NotNull
    DeviceType deviceType;
    @NotNull
    Volume volume;
    @NotNull
    DeviceStatus status;

    @Override
    @NotNull
    public String getId() {
        return deviceId;
    }

    @Override
    @NotNull
    public String getName() {
        return deviceName;
    }

    @Override
    @NotNull
    public DeviceType getType() {
        return deviceType;
    }

    @Override
    @NotNull
    public DeviceStatus getStatus() {
        return status;
    }

    public Device withActiveStatus() {
        return withStatus(DeviceStatus.ACTIVE);
    }

    public Device withIdleStatus() {
        return withStatus(DeviceStatus.IDLE);
    }
}
