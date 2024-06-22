package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.entity.factory.DefaultPlayerStateEntityFactory;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.ShuffleMode;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.PauseCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.handler.PlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.support.mapper.CurrentPlayerState2CurrentlyPlayingPlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.defer;

@Component
public class DefaultPlayerOperations implements BasicPlayerOperations {
    private final DeviceOperations deviceOperations;
    private final PlayCommandHandlerDelegate playCommandHandlerDelegate;
    private final CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter;
    private final PauseCommandHandlerDelegate pauseCommandHandlerDelegate;

    private final PlayerStateService playerStateService;

    private final Logger logger = LoggerFactory.getLogger(DefaultPlayerOperations.class);

    public DefaultPlayerOperations(final PlayerStateRepository playerStateRepository, final DeviceOperations deviceOperations,
                                   final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport,
                                   final PlayCommandHandlerDelegate playCommandHandlerDelegate,
                                   final CurrentPlayerState2CurrentlyPlayingPlayerStateConverter playerStateConverter,
                                   final PauseCommandHandlerDelegate pauseCommandHandlerDelegate) {
        this.deviceOperations = deviceOperations;
        this.playCommandHandlerDelegate = playCommandHandlerDelegate;
        this.playerStateConverter = playerStateConverter;
        this.pauseCommandHandlerDelegate = pauseCommandHandlerDelegate;
        this.playerStateService = new PlayerStateService(playerStateRepository, playerStateConverterSupport,
                new DefaultPlayerStateEntityFactory(new DeviceEntity.Factory(), new TrackItemEntity.Factory()));
    }


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
                .map(state -> state.withShuffleState(shuffleMode))
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
                                                 @Nullable final PlayCommandContext context,
                                                 @Nullable final TargetDevice targetDevice) {
        return playCommandHandlerDelegate.playOrResume(user, context, targetDevice);
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> pause(@NotNull User user) {
        return pauseCommandHandlerDelegate.pause(user);
    }

    @NotNull
    private Mono<CurrentPlayerState> saveEmptyPlayerStateFor(@NotNull final User user) {
        final CurrentPlayerState state = CurrentPlayerState.emptyFor(user);

        return playerStateService.save(state)
                .doOnNext(it -> logger.info("Created new empty player state due to missing for the user: {}", user));
    }
}
