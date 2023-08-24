package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.Devices;
import com.odeyalo.sonata.connect.entity.InMemoryDevice;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.DevicesModel;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DefaultStorageDeviceOperations implements DeviceOperations {
    final PlayerStateStorage playerStateStorage;

    public DefaultStorageDeviceOperations(PlayerStateStorage playerStateStorage) {
        this.playerStateStorage = playerStateStorage;
    }


    @Override
    public Mono<CurrentPlayerState> addDevice(User user, DeviceModel device) {
        return playerStateStorage.findByUserId(user.getId())
                .doOnNext(state -> state.getDevices().addDevice(createDevice(device)))
                .map(DefaultStorageDeviceOperations::convertToState);
    }

    @NotNull
    @Override
    public Mono<Boolean> containsById(User user, String deviceId) {
        return null;
    }

    @NotNull
    @Override
    public Mono<DevicesModel> getConnectedDevices(User user) {
        return playerStateStorage.findByUserId(user.getId())
                .map(PersistablePlayerState::getDevices)
                .map(DefaultStorageDeviceOperations::toDeviceModels)
                .map(DevicesModel::of);
    }

    @NotNull
    private static List<DeviceModel> toDeviceModels(Devices devices) {
        return devices.stream().map(device -> DeviceModel.of(device.getId(), device.getName(), device.getDeviceType(), device.getVolume(), true)).toList();
    }

    private static CurrentPlayerState convertToState(PersistablePlayerState state) {
        return CurrentPlayerState.builder()
                .id(state.getId())
                .playingType(state.getPlayingType())
                .playing(state.isPlaying())
                .shuffleState(state.getShuffleState())
                .progressMs(state.getProgressMs())
                .repeatState(state.getRepeatState())
                .devices(toDevicesModel(state.getDevices()))
                .build();
    }

    private static DevicesModel toDevicesModel(Devices devices) {
        List<DeviceModel> deviceModels = devices.stream().map(DefaultStorageDeviceOperations::toDeviceModel).toList();
        return DevicesModel.builder().devices(deviceModels).build();
    }

    private static DeviceModel toDeviceModel(Device device) {
        return DeviceModel.builder()
                .deviceId(device.getId())
                .deviceName(device.getName())
                .deviceType(device.getDeviceType())
                .volume(device.getVolume())
                .active(device.isActive())
                .build();
    }

    private static InMemoryDevice createDevice(DeviceModel device) {
        return InMemoryDevice.builder()
                .id(device.getDeviceId())
                .name(device.getDeviceName())
                .volume(device.getVolume())
                .deviceType(device.getDeviceType())
                .active(device.isActive())
                .build();
    }
}
