package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultStorageDeviceOperations implements DeviceOperations {
    private final PlayerStateRepository playerStateRepository;
    private final TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate;
    private final PlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport;
    private final PlayerStateService playerStateService;

    public DefaultStorageDeviceOperations(PlayerStateRepository playerStateRepository,
                                          TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate,
                                          PlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport) {
        this.playerStateRepository = playerStateRepository;
        this.transferPlaybackCommandHandlerDelegate = transferPlaybackCommandHandlerDelegate;
        this.currentPlayerStateConverterSupport = currentPlayerStateConverterSupport;
        this.playerStateService = null;
    }

    @Autowired
    public DefaultStorageDeviceOperations(PlayerStateService playerStateService,
                                          TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate) {
        this.playerStateService = playerStateService;
        this.playerStateRepository = playerStateService.getPlayerStateRepository();
        this.currentPlayerStateConverterSupport = playerStateService.getPlayerStateConverter();
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
    public Mono<CurrentPlayerState> disconnectDevice(User user, DisconnectDeviceArgs args) {
        return playerStateRepository.findByUserId(user.getId())
                .doOnNext(playerState -> playerState.getDevices().removeDevice(args.getDeviceId()))
                .map(currentPlayerStateConverterSupport::convertTo);
    }

    @NotNull
    @Override
    public Mono<Devices> getConnectedDevices(@NotNull final User user) {
        return playerStateService.loadPlayerState(user)
                .map(CurrentPlayerState::getDevices);
    }
}
