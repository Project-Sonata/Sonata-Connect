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
public class PlayerState {
    Long id;
    boolean playing;
    RepeatState repeatState;
    boolean shuffleState;
    Long progressMs;
    PlayingType playingType;
    DevicesEntity devicesEntity;
    UserEntity user;
    PlayableItemEntity currentlyPlayingItem;

    public static final boolean SHUFFLE_ENABLED = true;
    public static final boolean SHUFFLE_DISABLED = false;

    public boolean getShuffleState() {
        return shuffleState;
    }

    public PlayingType getCurrentlyPlayingType() {
        return playingType;
    }

    public DevicesEntity getDevicesEntity() {
        return devicesEntity;
    }

    public PlayableItemEntity getCurrentlyPlayingItem() {
        return currentlyPlayingItem;
    }

    public DevicesEntity getDevices() {
        return devicesEntity;
    }
}
