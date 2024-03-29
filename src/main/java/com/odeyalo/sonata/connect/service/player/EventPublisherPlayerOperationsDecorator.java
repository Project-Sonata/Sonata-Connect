package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerStateUpdatedPlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * Decorator that can publish event to PlayerSynchronizationManager
 */
public class EventPublisherPlayerOperationsDecorator implements BasicPlayerOperations {
    private final BasicPlayerOperations delegate;
    private final PlayerSynchronizationManager synchronizationManager;
    private final DeviceOperations deviceOperations;

    public EventPublisherPlayerOperationsDecorator(BasicPlayerOperations delegate, PlayerSynchronizationManager synchronizationManager, DeviceOperations deviceOperations) {
        this.delegate = delegate;
        this.synchronizationManager = synchronizationManager;
        this.deviceOperations = deviceOperations;
    }

    @Override
    public Mono<CurrentPlayerState> currentState(User user) {
        return delegate.currentState(user);
    }

    @Override
    public Mono<CurrentlyPlayingPlayerState> currentlyPlayingState(User user) {
        return delegate.currentlyPlayingState(user);
    }

    @Override
    public Mono<CurrentPlayerState> changeShuffle(User user, boolean shuffleMode) {
        return delegate.changeShuffle(user, shuffleMode);
    }

    @Override
    public DeviceOperations getDeviceOperations() {
        return deviceOperations;
    }

    @Override
    public Mono<CurrentPlayerState> playOrResume(User user, PlayCommandContext context, TargetDevice targetDevice) {
        return delegate.playOrResume(user, context, targetDevice);
    }

    @NotNull
    private Mono<CurrentPlayerState> publishEvent(CurrentPlayerState currentPlayerState, User user) {
        Device activeDevice = getActiveDevice(currentPlayerState);
        if (activeDevice == null) {
            return Mono.just(currentPlayerState);
        }

        PlayerStateUpdatedPlayerEvent stateUpdatedPlayerEvent = PlayerStateUpdatedPlayerEvent.builder()
                .playerState(currentPlayerState)
                .deviceThatChanged(activeDevice.getDeviceId())
                .build();

        return synchronizationManager.publishUpdatedState(user, stateUpdatedPlayerEvent)
                .thenReturn(currentPlayerState);
    }

    @Nullable
    private static Device getActiveDevice(CurrentPlayerState state) {
        return state.getDevices().getActiveDevice().orElse(null);
    }
}
