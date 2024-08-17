package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.service.player.handler.PauseCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.support.mapper.CurrentPlayerState2CurrentlyPlayingPlayerStateConverter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.defer;

@Component
@RequiredArgsConstructor
public final class DefaultPlayerOperations implements BasicPlayerOperations {
    private final DeviceOperations deviceOperations;
    private final PlayCommandHandlerDelegate playCommandHandlerDelegate;
    private final PauseCommandHandlerDelegate pauseCommandHandlerDelegate;
    private final CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter;
    private final PlayerStateService playerStateService;

    private final Logger logger = LoggerFactory.getLogger(DefaultPlayerOperations.class);


    @Override
    @NotNull
    public Mono<CurrentPlayerState> currentState(@NotNull final User user) {
        return playerStateService.loadPlayerState(user)
                .switchIfEmpty(defer(() -> saveEmptyPlayerStateFor(user)));
    }

    @Override
    @NotNull
    public Mono<CurrentlyPlayingPlayerState> currentlyPlayingState(@NotNull final User user) {
        return currentState(user)
                .filter(CurrentPlayerState::isPlaying)
                .map(playerStateConverter::convertTo);
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> changeShuffle(@NotNull final User user,
                                                  @NotNull final ShuffleMode shuffleMode) {
        return playerStateService.loadPlayerState(user)
                .map(playerState -> playerState.switchShuffleMode(shuffleMode))
                .flatMap(playerStateService::save);
    }

    @Override
    @NotNull
    public DeviceOperations getDeviceOperations() {
        return deviceOperations;
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> playOrResume(@NotNull final User user,
                                                 @NotNull final PlayCommandContext context,
                                                 @Nullable final TargetDevice targetDevice) {
        return playCommandHandlerDelegate.playOrResume(user, context, targetDevice);
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> pause(@NotNull User user) {
        return pauseCommandHandlerDelegate.pause(user);
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> changeVolume(@NotNull final User user,
                                                 @NotNull final Volume volume) {

        return playerStateService.loadPlayerState(user)
                .map(playerState -> playerState.changeVolume(volume))
                .flatMap(playerStateService::save);
    }

    @NotNull
    public Mono<CurrentPlayerState> seekToPosition(@NotNull final User user,
                                                   @NotNull final SeekPosition position) {
        return playerStateService.loadPlayerState(user)
                .map(playerState -> playerState.seekTo(position))
                .flatMap(playerStateService::save);
    }

    @NotNull
    private Mono<CurrentPlayerState> saveEmptyPlayerStateFor(@NotNull final User user) {
        final CurrentPlayerState state = CurrentPlayerState.emptyFor(user);

        return playerStateService.save(state)
                .doOnNext(it -> logger.info("Created new empty player state due to missing for the user: {}", user));
    }
}
