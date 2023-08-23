package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Immutable object to represent the currently playing object with player state
 */
@Value
@AllArgsConstructor(staticName = "of")
public class CurrentlyPlayingPlayerState {
    Boolean shuffleState;
}
