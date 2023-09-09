package com.odeyalo.sonata.connect.repository.storage;

import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * PlayerState that does not depend on specific database.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersistablePlayerState implements PlayerState {
    Long id;
    boolean playing;
    RepeatState repeatState;
    boolean shuffleState;
    Long progressMs;
    PlayingType playingType;
    DevicesEntity devicesEntity;
    UserEntity user;
    PlayableItemEntity currentlyPlayingItem;

    @Override
    public boolean getShuffleState() {
        return shuffleState;
    }

    @Override
    public PlayingType getCurrentlyPlayingType() {
        return playingType;
    }

    public DevicesEntity getDevicesEntity() {
        return devicesEntity;
    }

    @Override
    public PlayableItemEntity getCurrentlyPlayingItem() {
        return currentlyPlayingItem;
    }

    @Override
    public DevicesEntity getDevices() {
        return devicesEntity;
    }
}
