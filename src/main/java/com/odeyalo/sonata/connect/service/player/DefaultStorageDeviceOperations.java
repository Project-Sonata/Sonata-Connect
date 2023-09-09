package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.InMemoryDeviceEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.apache.commons.lang.BooleanUtils.negate;

@Component
public class DefaultStorageDeviceOperations implements DeviceOperations {
    private final PlayerStateStorage playerStateStorage;
    private final TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate;

    public DefaultStorageDeviceOperations(PlayerStateStorage playerStateStorage, TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate) {
        this.playerStateStorage = playerStateStorage;
        this.transferPlaybackCommandHandlerDelegate = transferPlaybackCommandHandlerDelegate;
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> addDevice(User user, Device device) {
        return playerStateStorage.findByUserId(user.getId())
                .doOnNext(state -> state.getDevicesEntity().addDevice(createDevice(device, state)))
                .map(DefaultStorageDeviceOperations::convertToState);
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

    private static CurrentPlayerState convertToState(PersistablePlayerState state) {
        return CurrentPlayerState.builder()
                .id(state.getId())
                .playingType(state.getPlayingType())
                .playing(state.isPlaying())
                .shuffleState(state.getShuffleState())
                .progressMs(state.getProgressMs())
                .repeatState(state.getRepeatState())
                .devices(toDevicesModel(state.getDevicesEntity()))
                .build();
    }

    private static Devices toDevicesModel(DevicesEntity devicesEntity) {
        List<Device> deviceModels = devicesEntity.stream().map(DefaultStorageDeviceOperations::toDeviceModel).toList();
        return Devices.builder().devices(deviceModels).build();
    }

    private static Device toDeviceModel(DeviceEntity deviceEntity) {
        return Device.builder()
                .deviceId(deviceEntity.getId())
                .deviceName(deviceEntity.getName())
                .deviceType(deviceEntity.getDeviceType())
                .volume(deviceEntity.getVolume())
                .active(deviceEntity.isActive())
                .build();
    }

    private static InMemoryDeviceEntity createDevice(Device device, PersistablePlayerState state) {
        Boolean isActive = negate(containAnyActiveDevice(state));
        return InMemoryDeviceEntity.builder()
                .id(device.getDeviceId())
                .name(device.getDeviceName())
                .volume(device.getVolume())
                .active(isActive)
                .deviceType(device.getDeviceType())
                .build();
    }

    private static boolean containAnyActiveDevice(PersistablePlayerState state) {
        return state.getDevicesEntity().stream().anyMatch(DeviceEntity::isActive);
    }
}
