package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.PauseCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.support.factory.PlayerStateFactory;
import com.odeyalo.sonata.connect.service.support.mapper.CurrentPlayerState2CurrentlyPlayingPlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DefaultPlayerOperations implements BasicPlayerOperations {
    private final PlayerStateRepository playerStateRepository;
    private final DeviceOperations deviceOperations;
    private final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport;
    private final PlayCommandHandlerDelegate playCommandHandlerDelegate;
    private final CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter;
    private final PauseCommandHandlerDelegate pauseCommandHandlerDelegate;
    private final Logger logger = LoggerFactory.getLogger(DefaultPlayerOperations.class);


    @Override
    public Mono<CurrentPlayerState> currentState(User user) {
        return playerStateRepository.findByUserId(user.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    PlayerStateEntity state = emptyState(user);
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

    @Override
    public Mono<CurrentPlayerState> pause(User user) {
        return pauseCommandHandlerDelegate.pause(user);
    }

    private static PlayerStateEntity emptyState(User user) {
        return PlayerStateFactory.createEmpty(user);
    }

    @NotNull
    private static PlayerStateEntity doChangeShuffleMode(PlayerStateEntity state, boolean shuffleMode) {
        state.setShuffleState(shuffleMode);
        return state;
    }
}
