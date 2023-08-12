package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.Devices;
import com.odeyalo.sonata.connect.model.DeviceType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    PlayerStateStorage playerStateStorage;

    @GetMapping("/state")
    public Mono<?> currentPlayerState() {
        return playerStateStorage.findById(1L)
                .map(state -> PlayerStateDto.builder()
                        .currentlyPlayingType(state.getCurrentlyPlayingType().name().toLowerCase())
                        .isPlaying(state.isPlaying())
                        .repeatState(state.getRepeatState())
                        .progressMs(state.getProgressMs())
                        .devices(toDevicesDto(state.getDevices()))
                        .shuffleState(state.getShuffleState())
                        .build());
    }

    private static DevicesDto toDevicesDto(Devices devices) {
        List<DeviceDto> dtos = devices.stream().map(PlayerController::toDeviceDto).toList();
        return DevicesDto.builder().devices(dtos).build();
    }

    private static DeviceDto toDeviceDto(Device device) {
        return DeviceDto.builder()
                .deviceId(device.getId())
                .deviceName(device.getName())
                .deviceType(device.getDeviceType())
                .volume(device.getVolume())
                .active(device.isActive())
                .build();
    }
}
