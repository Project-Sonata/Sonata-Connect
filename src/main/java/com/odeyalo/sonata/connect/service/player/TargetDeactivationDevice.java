package com.odeyalo.sonata.connect.service.player;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Immutable class to store single device that should be deactivated
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class TargetDeactivationDevice {
    @NonNull
    String deviceId;
}