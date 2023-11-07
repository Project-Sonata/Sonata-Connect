package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.PlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.support.factory.PlayerStateFactory;
import com.odeyalo.sonata.connect.service.support.mapper.CurrentPlayerState2CurrentlyPlayingPlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultPlayerOperations implements BasicPlayerOperations {
    private final PlayerStateRepository playerStateRepository;
    private final DeviceOperations deviceOperations;
    private final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport;
    private final PlayCommandHandlerDelegate playCommandHandlerDelegate;
    private final CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter;
    private final Logger logger = LoggerFactory.getLogger(DefaultPlayerOperations.class);

    public DefaultPlayerOperations(PlayerStateRepository playerStateRepository,
                                   DeviceOperations deviceOperations,
                                   PlayerState2CurrentPlayerStateConverter playerStateConverterSupport, PlayCommandHandlerDelegate playCommandHandlerDelegate, CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter) {
        this.playerStateRepository = playerStateRepository;
        this.deviceOperations = deviceOperations;
        this.playerStateConverterSupport = playerStateConverterSupport;
        this.playCommandHandlerDelegate = playCommandHandlerDelegate;
        this.playerStateConverter = playerStateConverter;
    }

    @Override
    public Mono<CurrentPlayerState> currentState(User user) {
        return playerStateRepository.findByUserId(user.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    PlayerState state = emptyState(user);
                    logger.info("Created new empty player state due to missing for the user: {}", user);
                    return playerStateRepository.save(state);
                }))
                .map(playerStateConverterSupport::convertTo);
    }

    @Override
    public Mono<CurrentlyPlayingPlayerState> currentlyPlayingState(User user) {
        return currentState(user)
                .filter(CurrentPlayerState::isPlaying)
                .map(playerStateConverter::convertTo);
    }

    @Override
    public Mono<CurrentPlayerState> changeShuffle(User user, boolean shuffleMode) {
        return playerStateRepository.findByUserId(user.getId())
                .map(state -> doChangeShuffleMode(state, shuffleMode))
                .flatMap(playerStateRepository::save)
                .map(playerStateConverterSupport::convertTo);
    }

    @Override
    public DeviceOperations getDeviceOperations() {
        return deviceOperations;
    }

    @Override
    public Mono<CurrentPlayerState> playOrResume(User user, PlayCommandContext context, TargetDevice targetDevice) {
        return playCommandHandlerDelegate.playOrResume(user, context, targetDevice);
    }

    private static PlayerState emptyState(User user) {
        return PlayerStateFactory.createEmpty(user);
    }

    @NotNull
    private static PlayerState doChangeShuffleMode(PlayerState state, boolean shuffleMode) {
        state.setShuffleState(shuffleMode);
        return state;
    }
}
