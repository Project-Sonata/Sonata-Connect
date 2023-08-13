package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryPlayerState implements PlayerState {
    Long id;
    boolean playing;
    RepeatState repeatState;
    boolean shuffleState;
    Long progressMs;
    PlayingType playingType;
    Devices devices;
    UserEntity user;

    @Override
    public boolean getShuffleState() {
        return shuffleState;
    }

    @Override
    public PlayingType getCurrentlyPlayingType() {
        return playingType;
    }

    @Override
    public Devices getDevices() {
        return devices;
    }
}
