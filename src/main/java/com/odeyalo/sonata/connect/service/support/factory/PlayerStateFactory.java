package com.odeyalo.sonata.connect.service.support.factory;

import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.User;

import java.util.Random;

public class PlayerStateFactory {

    public static PlayerState createEmpty(User user) {
        return PlayerState.builder()
                .user(new UserEntity(user.getId()))
                .id(new Random().nextLong(0, Long.MAX_VALUE))
                .repeatState(RepeatState.OFF)
                .shuffleState(false)
                .progressMs(-1L)
                .playingType(null)
                .playing(false)
                .devicesEntity(DevicesEntity.empty())
                .currentlyPlayingItem(null)
                .build();
    }
}
