package com.odeyalo.sonata.connect.repository.storage.support;

import com.odeyalo.sonata.connect.entity.InMemoryPlayerState;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import org.springframework.stereotype.Component;

@Component
public class InMemory2PersistablePlayerStateConverter implements PersistableEntityConverter<InMemoryPlayerState, PersistablePlayerState> {

    @Override
    public PersistablePlayerState convertTo(InMemoryPlayerState type) {
        return PersistablePlayerState.builder()
                .id(type.getId())
                .playingType(type.getPlayingType())
                .playing(type.isPlaying())
                .shuffleState(type.getShuffleState())
                .progressMs(type.getProgressMs())
                .repeatState(type.getRepeatState())
                .user(type.getUser())
                .devicesEntity(type.getDevicesEntity())
                .currentlyPlayingItem(type.getCurrentlyPlayingItem())
                .build();
    }

    @Override
    public InMemoryPlayerState convertFrom(PersistablePlayerState type) {
        return InMemoryPlayerState.builder()
                .id(type.getId())
                .playingType(type.getPlayingType())
                .playing(type.isPlaying())
                .shuffleState(type.getShuffleState())
                .progressMs(type.getProgressMs())
                .repeatState(type.getRepeatState())
                .user(type.getUser())
                .devicesEntity(type.getDevicesEntity())
                .currentlyPlayingItem(type.getCurrentlyPlayingItem())
                .build();
    }
}
