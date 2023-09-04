package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import org.springframework.stereotype.Component;

/**
 * Convert CurrentPlayerState to CurrentlyPlayingPlayerState
 */
@Component
public class CurrentPlayerState2CurrentlyPlayingPlayerStateConverter implements Converter<CurrentPlayerState, CurrentlyPlayingPlayerState> {

    @Override
    public CurrentlyPlayingPlayerState convertTo(CurrentPlayerState state) {
        return CurrentlyPlayingPlayerState.builder()
                .playableItem(state.getPlayableItem())
                .shuffleState(state.getShuffleState())
                .repeatState(state.getRepeatState())
                .playing(state.isPlaying())
                .currentlyPlayingType(state.getPlayingType())
                .devices(state.getDevices())
                .build();
    }
}
