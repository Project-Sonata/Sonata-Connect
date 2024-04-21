package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable object to represent the currently playing object with player state
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class CurrentlyPlayingPlayerState {
    @NotNull
    PlayingType currentlyPlayingType;
    @NotNull
    RepeatState repeatState;
    @NotNull
    PlayableItem playableItem;
    @NotNull
    Devices devices;
    long progressMs;
    Boolean shuffleState;
    boolean playing;
}
