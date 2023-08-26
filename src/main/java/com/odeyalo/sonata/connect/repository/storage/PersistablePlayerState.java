package com.odeyalo.sonata.connect.repository.storage;

import com.odeyalo.sonata.connect.entity.Devices;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.DeviceType;
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
    Devices devices;
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

    @Override
    public Devices getDevices() {
        return devices;
    }

    @Override
    public PlayableItemEntity getCurrentlyPlayingItem() {
        return currentlyPlayingItem;
    }
}
