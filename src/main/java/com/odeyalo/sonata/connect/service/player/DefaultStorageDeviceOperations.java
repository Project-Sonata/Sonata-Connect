package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.factory.DeviceEntityFactory;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.model.DeviceSpec.DeviceStatus;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.DevicesEntity2DevicesConverter;
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
    private final DevicesEntity2DevicesConverter devicesEntity2DevicesConverter;
    private final DeviceEntityFactory deviceEntityFactory;
    private final PlayerStateService playerStateService;

    public DefaultStorageDeviceOperations(PlayerStateRepository playerStateRepository,
                                          TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate,
                                          PlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport,
                                          DevicesEntity2DevicesConverter devicesEntity2DevicesConverter,
                                          DeviceEntityFactory deviceEntityFactory) {
        this.playerStateRepository = playerStateRepository;
        this.transferPlaybackCommandHandlerDelegate = transferPlaybackCommandHandlerDelegate;
        this.currentPlayerStateConverterSupport = currentPlayerStateConverterSupport;
        this.devicesEntity2DevicesConverter = devicesEntity2DevicesConverter;
        this.deviceEntityFactory = deviceEntityFactory;
        this.playerStateService = null;
    }

    @Autowired
    public DefaultStorageDeviceOperations(PlayerStateService playerStateService,
                                          TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate,
                                          DevicesEntity2DevicesConverter devicesEntity2DevicesConverter,
                                          DeviceEntityFactory deviceEntityFactory) {
        this.playerStateService = playerStateService;
        this.playerStateRepository = playerStateService.getPlayerStateRepository();
        this.currentPlayerStateConverterSupport = playerStateService.getPlayerStateConverter();
        this.transferPlaybackCommandHandlerDelegate = transferPlaybackCommandHandlerDelegate;
        this.devicesEntity2DevicesConverter = devicesEntity2DevicesConverter;
        this.deviceEntityFactory = deviceEntityFactory;
    }


    @NotNull
    @Override
    public Mono<CurrentPlayerState> addDevice(@NotNull final User user,
                                              @NotNull final Device device) {
        return playerStateService.loadPlayerState(user)
                .map(state -> {
                    Devices devices = state.getDevices().connectDevice(device);
                    return state.withDevices(devices);
                })
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
    public Mono<Devices> getConnectedDevices(User user) {
        return playerStateRepository.findByUserId(user.getId())
                .map(PlayerStateEntity::getDevicesEntity)
                .map(devicesEntity2DevicesConverter::convertTo);
    }

    @NotNull
    private DeviceEntity createDeviceEntity(@NotNull final Device device,
                                            @NotNull final PlayerStateEntity state) {

        final DeviceStatus status = state.hasActiveDevice() ?
                DeviceStatus.IDLE :
                DeviceStatus.ACTIVE;

        return deviceEntityFactory.create(device, status);
    }
}
