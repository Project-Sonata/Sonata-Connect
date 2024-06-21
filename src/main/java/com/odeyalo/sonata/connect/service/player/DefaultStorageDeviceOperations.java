package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultStorageDeviceOperations implements DeviceOperations {
    private final TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate;
    private final PlayerStateService playerStateService;

    @Autowired
    public DefaultStorageDeviceOperations(PlayerStateService playerStateService,
                                          TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate) {
        this.playerStateService = playerStateService;
        this.transferPlaybackCommandHandlerDelegate = transferPlaybackCommandHandlerDelegate;
    }


    @NotNull
    @Override
    public Mono<CurrentPlayerState> addDevice(@NotNull final User user,
                                              @NotNull final Device device) {
        return playerStateService.loadPlayerState(user)
                .map(playerState -> playerState.connectDevice(device))
                .flatMap(playerStateService::save);
    }

    @NotNull
    @Override
    public Mono<Boolean> containsById(User user, String deviceId) {
        return Mono.empty();
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> transferPlayback(User user, SwitchDeviceCommandArgs args, TargetDeactivationDevices deactivationDevices, TargetDevices targetDevices) {
        return transferPlaybackCommandHandlerDelegate.transferPlayback(user, args, deactivationDevices, targetDevices);
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> disconnectDevice(@NotNull final User user,
                                                     @NotNull final DisconnectDeviceArgs args) {
        return playerStateService.loadPlayerState(user)
                .map(playerState -> playerState.disconnectDevice(args.getDeviceId()))
                .flatMap(playerStateService::save);
    }

    @NotNull
    @Override
    public Mono<Devices> getConnectedDevices(@NotNull final User user) {
        return playerStateService.loadPlayerState(user)
                .map(CurrentPlayerState::getDevices);
    }
}
