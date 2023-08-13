package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.Devices;
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
public class DefaultBasicPlayerOperations implements BasicPlayerOperations {
    final PlayerStateStorage playerStateStorage;

    public DefaultBasicPlayerOperations(PlayerStateStorage playerStateStorage) {
        this.playerStateStorage = playerStateStorage;
    }

    @Override
    public Mono<CurrentPlayerState> currentState(User user) {
        return playerStateStorage.findByUserId(user.getId())
                .map(DefaultBasicPlayerOperations::convertToState);
    }

    @Override
    public Mono<CurrentPlayerState> changeShuffle(User user, boolean shuffleMode) {
        return playerStateStorage.findByUserId(user.getId())
                .map(state -> negateShuffleMode(state, shuffleMode))
                .flatMap(playerStateStorage::save)
                .map(DefaultBasicPlayerOperations::convertToState);
    }

    @NotNull
    private static PersistablePlayerState negateShuffleMode(PersistablePlayerState state, boolean shuffleMode) {
        state.setShuffleState(shuffleMode);
        return state;
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
        List<DeviceModel> deviceModels = devices.stream().map(DefaultBasicPlayerOperations::toDeviceModel).toList();
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
}
