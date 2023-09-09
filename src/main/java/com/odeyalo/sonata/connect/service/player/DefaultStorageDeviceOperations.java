package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PersistableDeviceEntity;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.PersistablePlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.apache.commons.lang.BooleanUtils.negate;

@Component
public class DefaultStorageDeviceOperations implements DeviceOperations {
    private final PlayerStateStorage playerStateStorage;
    private final TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate;
    private final PersistablePlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport;

    public DefaultStorageDeviceOperations(PlayerStateStorage playerStateStorage, TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate, PersistablePlayerState2CurrentPlayerStateConverter currentPlayerStateConverterSupport) {
        this.playerStateStorage = playerStateStorage;
        this.transferPlaybackCommandHandlerDelegate = transferPlaybackCommandHandlerDelegate;
        this.currentPlayerStateConverterSupport = currentPlayerStateConverterSupport;
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> addDevice(User user, Device device) {
        return playerStateStorage.findByUserId(user.getId())
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
        return playerStateStorage.findByUserId(user.getId())
                .map(PersistablePlayerState::getDevicesEntity)
                .map(DefaultStorageDeviceOperations::toDeviceModels)
                .map(Devices::of);
    }

    @NotNull
    private static List<Device> toDeviceModels(DevicesEntity devicesEntity) {
        return devicesEntity.stream().map(device -> Device.of(device.getId(), device.getName(), device.getDeviceType(), device.getVolume(), device.isActive())).toList();
    }

    private static PersistableDeviceEntity createDevice(Device device, PersistablePlayerState state) {
        Boolean isActive = negate(containAnyActiveDevice(state));
        return buildDeviceEntity(device, isActive);
    }

    private static boolean containAnyActiveDevice(PersistablePlayerState state) {
        return state.getDevicesEntity().stream().anyMatch(DeviceEntity::isActive);
    }

    private static PersistableDeviceEntity buildDeviceEntity(Device device, boolean isActive) {
        return PersistableDeviceEntity.builder()
                .id(device.getDeviceId())
                .name(device.getDeviceName())
                .volume(device.getVolume())
                .active(isActive)
                .deviceType(device.getDeviceType())
                .build();
    }
}
