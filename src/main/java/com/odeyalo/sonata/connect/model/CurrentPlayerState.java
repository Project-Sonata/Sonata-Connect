package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represent a current state pf the player despite the fact is playing something or not
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
@With
public class CurrentPlayerState {
    long id;
    boolean playing;
    @NotNull
    @Builder.Default
    RepeatState repeatState = RepeatState.OFF;
    boolean shuffleState;
    @Nullable
    Long progressMs;
    @Nullable
    PlayingType playingType;
    @NotNull
    Devices devices;
    @Nullable
    PlayableItem playableItem;
    @NotNull
    User user;
    long lastPauseTime = 0;
    long playStartTime = 0;

    public boolean getShuffleState() {
        return shuffleState;
    }

    public PlayableItem getPlayingItem() {
        return playableItem;
    }
}
