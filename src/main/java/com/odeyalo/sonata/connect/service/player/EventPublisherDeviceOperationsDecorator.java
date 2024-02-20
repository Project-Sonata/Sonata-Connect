package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.player.sync.event.DeviceConnectedPlayerEvent;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
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
                .zipWith(ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).cast(AuthenticatedUser.class))
                .flatMap(tuple -> {
                    CurrentPlayerState state = tuple.getT1();
                    String deviceId = device.getDeviceId();
                    return synchronizationManager.publishUpdatedState(tuple.getT2(), DeviceConnectedPlayerEvent.of(state, deviceId))
                            .thenReturn(state);
                });
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
        return delegate.disconnectDevice(user, args);
    }

    @NotNull
    @Override
    public Mono<Devices> getConnectedDevices(User user) {
        return delegate.getConnectedDevices(user);
    }
}
