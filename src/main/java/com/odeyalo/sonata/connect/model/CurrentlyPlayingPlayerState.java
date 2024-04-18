package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * Immutable object to represent the currently playing object with player state
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class CurrentlyPlayingPlayerState {
    Boolean shuffleState;
    boolean playing;
    PlayingType currentlyPlayingType;
    RepeatState repeatState;
    PlayableItem playableItem;
    Devices devices;
    Long progressMs;
}
