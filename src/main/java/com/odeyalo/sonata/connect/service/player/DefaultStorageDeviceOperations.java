package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.DevicesEntity2DevicesConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultStorageDeviceOperations implements DeviceOperations {
    private final PlayerStateRepository playerStateRepository;
    private final TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate;
    private final PlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport;
    private final DevicesEntity2DevicesConverter devicesEntity2DevicesConverter;

    public DefaultStorageDeviceOperations(PlayerStateRepository playerStateRepository,
                                          TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate,
                                          PlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport, DevicesEntity2DevicesConverter devicesEntity2DevicesConverter) {
        this.playerStateRepository = playerStateRepository;
        this.transferPlaybackCommandHandlerDelegate = transferPlaybackCommandHandlerDelegate;
        this.currentPlayerStateConverterSupport = currentPlayerStateConverterSupport;
        this.devicesEntity2DevicesConverter = devicesEntity2DevicesConverter;
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> addDevice(User user, Device device) {
        return playerStateRepository.findByUserId(user.getId())
                .doOnNext(state -> state.getDevicesEntity().addDevice(createDeviceEntity(device, state)))
                .map(currentPlayerStateConverterSupport::convertTo);
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
    public Mono<Devices> getConnectedDevices(User user) {
        return playerStateRepository.findByUserId(user.getId())
                .map(PlayerState::getDevicesEntity)
                .map(devicesEntity2DevicesConverter::convertTo);
    }

    private static DeviceEntity createDeviceEntity(Device device, PlayerState state) {
        boolean isActive = doesNotContainActiveDevice(state);
        return fromDeviceToDeviceEntity(device, isActive);
    }

    private static boolean doesNotContainActiveDevice(PlayerState state) {
        return state.getDevicesEntity().hasNotActiveDevice();
    }

    private static DeviceEntity fromDeviceToDeviceEntity(Device device, boolean isActive) {
        return DeviceEntity.builder()
                .id(device.getDeviceId())
                .name(device.getDeviceName())
                .volume(device.getVolume())
                .active(isActive)
                .deviceType(device.getDeviceType())
                .build();
    }
}
