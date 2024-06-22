package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerStateUpdatedPlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import static com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent.EventType.PLAYER_STATE_UPDATED;

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
    @NotNull
    public Mono<CurrentPlayerState> playOrResume(@NotNull final User user,
                                                          @Nullable final PlayCommandContext context,
                                                          @Nullable final TargetDevice targetDevice) {
        return delegate.playOrResume(user, context, targetDevice)
                .flatMap(it -> publishEvent(it, PLAYER_STATE_UPDATED, user));
    }

    @Override
    public Mono<CurrentPlayerState> pause(User user) {
        return delegate.pause(user)
                .flatMap(it -> publishEvent(it, PLAYER_STATE_UPDATED, user));
    }

    @NotNull
    private Mono<CurrentPlayerState> publishEvent(@NotNull CurrentPlayerState currentPlayerState,
                                                  @NotNull PlayerEvent.EventType eventType,
                                                  @NotNull User user) {
        Device activeDevice = getActiveDevice(currentPlayerState);
        if (activeDevice == null) {
            return Mono.just(currentPlayerState);
        }

        PlayerStateUpdatedPlayerEvent stateUpdatedPlayerEvent = PlayerStateUpdatedPlayerEvent.builder()
                .playerState(currentPlayerState)
                .deviceThatChanged(activeDevice.getDeviceId())
                .eventType(eventType)
                .build();

        return synchronizationManager.publishUpdatedState(user, stateUpdatedPlayerEvent)
                .thenReturn(currentPlayerState);
    }

    @Nullable
    private static Device getActiveDevice(CurrentPlayerState state) {
        return state.getDevices().getActiveDevice().orElse(null);
    }
}
