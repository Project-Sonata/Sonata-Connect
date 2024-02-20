package com.odeyalo.sonata.connect.service.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * Arguments to disconnect device
 */
@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class DisconnectDeviceArgs {
    String deviceId;

    public static DisconnectDeviceArgs withDeviceId(String deviceId) {
        return builder().deviceId(deviceId).build();
    }
}
