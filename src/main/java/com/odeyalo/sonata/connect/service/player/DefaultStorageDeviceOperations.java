package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.apache.commons.lang.BooleanUtils.negate;

@Component
public class DefaultStorageDeviceOperations implements DeviceOperations {
    private final PlayerStateRepository playerStateRepository;
    private final TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate;
    private final PlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport;

    public DefaultStorageDeviceOperations(PlayerStateRepository playerStateRepository,
                                          TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate,
                                          PlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport) {
        this.playerStateRepository = playerStateRepository;
        this.transferPlaybackCommandHandlerDelegate = transferPlaybackCommandHandlerDelegate;
        this.currentPlayerStateConverterSupport = currentPlayerStateConverterSupport;
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> addDevice(User user, Device device) {
        return playerStateRepository.findByUserId(user.getId())
                .doOnNext(state -> state.getDevicesEntity().addDevice(createDevice(device, state)))
                .map(currentPlayerStateConverterSupport::convertTo);
    }

    @NotNull
    @Override
    public Mono<Boolean> containsById(User user, String deviceId) {
        return null;
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
                .map(DefaultStorageDeviceOperations::toDeviceModels)
                .map(Devices::of);
    }

    @NotNull
    private static List<Device> toDeviceModels(DevicesEntity devicesEntity) {
        return devicesEntity.stream().map(device -> Device.of(device.getId(), device.getName(), device.getDeviceType(), device.getVolume(), device.isActive())).toList();
    }

    private static DeviceEntity createDevice(Device device, PlayerState state) {
        Boolean isActive = negate(containAnyActiveDevice(state));
        return buildDeviceEntity(device, isActive);
    }

    private static boolean containAnyActiveDevice(PlayerState state) {
        return state.getDevicesEntity().stream().anyMatch(DeviceEntity::isActive);
    }

    private static DeviceEntity buildDeviceEntity(Device device, boolean isActive) {
        return DeviceEntity.builder()
                .id(device.getDeviceId())
                .name(device.getDeviceName())
                .volume(device.getVolume())
                .active(isActive)
                .deviceType(device.getDeviceType())
                .build();
    }
}
