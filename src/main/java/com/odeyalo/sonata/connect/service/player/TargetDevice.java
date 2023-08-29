package com.odeyalo.sonata.connect.service.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Device that should be used to play the track, episode, podcast, etc
 */
@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class TargetDevice {
    // Id of the device
    @NonNull
    String id;
}
