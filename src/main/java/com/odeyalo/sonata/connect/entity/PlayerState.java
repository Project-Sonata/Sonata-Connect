package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;

public interface PlayerState {
    boolean SHUFFLE_ENABLED = true;
    boolean SHUFFLE_DISABLED = false;

    Long getId();

    RepeatState getRepeatState();

    boolean getShuffleState();

    Long getProgressMs();

    PlayingType getCurrentlyPlayingType();

    boolean isPlaying();

    Devices getDevices();

    UserEntity getUser();
}
