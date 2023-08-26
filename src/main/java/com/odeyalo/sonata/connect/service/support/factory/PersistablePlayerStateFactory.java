package com.odeyalo.sonata.connect.service.support.factory;

import com.odeyalo.sonata.connect.entity.InMemoryDevices;
import com.odeyalo.sonata.connect.entity.InMemoryUserEntity;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;

public class PersistablePlayerStateFactory {

    public static PersistablePlayerState createEmpty(User user) {
        return PersistablePlayerState.builder()
                .user(new InMemoryUserEntity(user.getId()))
                .id(1L)
                .repeatState(RepeatState.OFF)
                .shuffleState(false)
                .progressMs(-1L)
                .playingType(null)
                .playing(false)
                .devices(InMemoryDevices.empty())
                .currentlyPlayingItem(null)
                .build();
    }
}
