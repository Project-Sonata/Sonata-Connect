package com.odeyalo.sonata.connect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class CurrentPlayerState {
    Long id;
    boolean playing;
    RepeatState repeatState;
    boolean shuffleState;
    Long progressMs;
    PlayingType playingType;
    Devices devices;
    PlayableItem playableItem;

    public boolean getShuffleState() {
        return shuffleState;
    }

    public PlayableItem getPlayingItem() {
        return playableItem;
    }
}
