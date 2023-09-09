package com.odeyalo.sonata.connect.service.support.factory;

import com.odeyalo.sonata.connect.entity.InMemoryDevicesEntity;
import com.odeyalo.sonata.connect.entity.InMemoryUserEntity;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;

import java.util.Random;

public class PersistablePlayerStateFactory {

    public static PersistablePlayerState createEmpty(User user) {
        return PersistablePlayerState.builder()
                .user(new InMemoryUserEntity(user.getId()))
                .id(new Random().nextLong(0, Long.MAX_VALUE))
                .repeatState(RepeatState.OFF)
                .shuffleState(false)
                .progressMs(-1L)
                .playingType(null)
                .playing(false)
                .devicesEntity(InMemoryDevicesEntity.empty())
                .currentlyPlayingItem(null)
                .build();
    }
}
