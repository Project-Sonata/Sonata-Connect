package com.odeyalo.sonata.connect.service.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * Immutable class to store arguments for switch device command received from client
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class SwitchDeviceCommandArgs {
    // True if playable item should be started on new device even if playback was paused.
    boolean ensurePlaybackStarted;

    public static SwitchDeviceCommandArgs ensurePlaybackStarted() {
        return of(true);
    }

    public static SwitchDeviceCommandArgs noMatter() {
        return of(false);
    }
}
