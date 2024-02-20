package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.player.sync.event.DeviceConnectedPlayerEvent;
import com.odeyalo.sonata.connect.service.player.sync.event.DeviceDisconnectedPlayerEvent;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Decorator that can publish event to PlayerSynchronizationManager
 */
public class EventPublisherDeviceOperationsDecorator implements DeviceOperations {
    private final DeviceOperations delegate;
    private final PlayerSynchronizationManager synchronizationManager;

    public EventPublisherDeviceOperationsDecorator(DeviceOperations delegate, PlayerSynchronizationManager synchronizationManager) {
        this.delegate = delegate;
        this.synchronizationManager = synchronizationManager;
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> addDevice(User user, Device device) {
        return delegate.addDevice(user, device)
                .flatMap(state -> synchronizationManager.publishUpdatedState(user,
                                DeviceConnectedPlayerEvent.builder()
                                        .playerState(state)
                                        .deviceThatChanged(device.getDeviceId())
                                        .build())
                        .thenReturn(state));

    }

    @NotNull
    @Override
    public Mono<Boolean> containsById(User user, String deviceId) {
        return delegate.containsById(user, deviceId);
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> transferPlayback(User user, SwitchDeviceCommandArgs args, TargetDeactivationDevices deactivationDevices, TargetDevices targetDevices) {
        return delegate.transferPlayback(user, args, deactivationDevices, targetDevices);
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> disconnectDevice(User user, DisconnectDeviceArgs args) {
        return delegate.disconnectDevice(user, args)
                .flatMap(currentPlayerState -> synchronizationManager
                        .publishUpdatedState(user, DeviceDisconnectedPlayerEvent.builder()
                                .playerState(currentPlayerState)
                                .deviceThatChanged(args.getDeviceId())
                                .build())
                        .thenReturn(currentPlayerState)
                );
    }

    @NotNull
    @Override
    public Mono<Devices> getConnectedDevices(User user) {
        return delegate.getConnectedDevices(user);
    }
}
