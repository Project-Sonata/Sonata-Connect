package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerStateUpdatedPlayerEvent;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
    private Mono<CurrentPlayerState> publishEvent(Tuple2<CurrentPlayerState, AuthenticatedUser> tuple) {
        CurrentPlayerState currentPlayerState = tuple.getT1();
        AuthenticatedUser authenticatedUser = tuple.getT2();
        Device activeDevice = getActiveDevice(currentPlayerState);
        if (activeDevice == null) {
            return Mono.just(currentPlayerState);
        }
        return synchronizationManager.publishUpdatedState(authenticatedUser,
                PlayerStateUpdatedPlayerEvent.of(currentPlayerState, activeDevice.getDeviceId())).thenReturn(currentPlayerState);
    }

    private static Device getActiveDevice(CurrentPlayerState state) {
        return state.getDevices().getDevices().stream().filter(Device::isActive).findFirst().orElse(null);
    }
}
