package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.service.support.factory.PersistablePlayerStateFactory;
import com.odeyalo.sonata.connect.service.support.mapper.PersistablePlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultPlayerOperations implements BasicPlayerOperations {
    private final PlayerStateStorage playerStateStorage;
    private final DeviceOperations deviceOperations;
    private final PersistablePlayerState2CurrentPlayerStateConverter playerStateConverterSupport;

    private final Logger logger = LoggerFactory.getLogger(DefaultPlayerOperations.class);

    public DefaultPlayerOperations(PlayerStateStorage playerStateStorage,
                                   DeviceOperations deviceOperations,
                                   PersistablePlayerState2CurrentPlayerStateConverter playerStateConverterSupport) {
        this.playerStateStorage = playerStateStorage;
        this.deviceOperations = deviceOperations;
        this.playerStateConverterSupport = playerStateConverterSupport;
    }

    @Override
    public Mono<CurrentPlayerState> currentState(User user) {
        return playerStateStorage.findByUserId(user.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    PersistablePlayerState state = emptyState(user);
                    logger.info("Created new empty player state due to missing for the user: {}", user);
                    return playerStateStorage.save(state);
                }))
                .map(playerStateConverterSupport::convertTo);
    }

    @Override
    public Mono<CurrentlyPlayingPlayerState> currentlyPlayingState(User user) {
        return currentState(user)
                .filter(CurrentPlayerState::isPlaying)
                .map(state -> CurrentlyPlayingPlayerState.of(state.getShuffleState()));
    }

    @Override
    public Mono<CurrentPlayerState> changeShuffle(User user, boolean shuffleMode) {
        return playerStateStorage.findByUserId(user.getId())
                .map(state -> doChangeShuffleMode(state, shuffleMode))
                .flatMap(playerStateStorage::save)
                .map(playerStateConverterSupport::convertTo);
    }

    @Override
    public DeviceOperations getDeviceOperations() {
        return deviceOperations;
    }


    private static PersistablePlayerState emptyState(User user) {
        return PersistablePlayerStateFactory.createEmpty(user);
    }

    @NotNull
    private static PersistablePlayerState doChangeShuffleMode(PersistablePlayerState state, boolean shuffleMode) {
        state.setShuffleState(shuffleMode);
        return state;
    }
}
