package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DefaultPlayerOperations implements BasicPlayerOperations {
    final PlayerStateStorage playerStateStorage;
    final DeviceOperations deviceOperations;
    final Logger logger = LoggerFactory.getLogger(DefaultPlayerOperations.class);

    public DefaultPlayerOperations(PlayerStateStorage playerStateStorage, DeviceOperations deviceOperations) {
        this.playerStateStorage = playerStateStorage;
        this.deviceOperations = deviceOperations;
    }

    @Override
    public Mono<CurrentPlayerState> currentState(User user) {
        return playerStateStorage.findByUserId(user.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    PersistablePlayerState state = emptyState(user);
                    logger.info("Created new empty player state due to missing for the user: {}", user);
                    return playerStateStorage.save(state);
                }))
                .map(DefaultPlayerOperations::convertToState);
    }

    @Override
    public Mono<CurrentlyPlayingPlayerState> currentlyPlayingState(User user) {
        return currentState(user)
                .filter(state -> state.isPlaying())
                .map(state -> CurrentlyPlayingPlayerState.of(state.getShuffleState()));
    }

    @Override
    public Mono<CurrentPlayerState> changeShuffle(User user, boolean shuffleMode) {
        return playerStateStorage.findByUserId(user.getId())
                .map(state -> negateShuffleMode(state, shuffleMode))
                .flatMap(playerStateStorage::save)
                .map(DefaultPlayerOperations::convertToState);
    }

    @Override
    public DeviceOperations getDeviceOperations() {
        return deviceOperations;
    }



    private static PersistablePlayerState emptyState(User user) {
        return PersistablePlayerState.builder()
                .user(new InMemoryUserEntity(user.getId()))
                .id(1L)
                .repeatState(RepeatState.OFF)
                .shuffleState(false)
                .progressMs(-1L)
                .playingType(null)
                .playing(false)
                .devices(InMemoryDevices.empty())
                .build();
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
        List<DeviceModel> deviceModels = devices.stream().map(DefaultPlayerOperations::toDeviceModel).toList();
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
