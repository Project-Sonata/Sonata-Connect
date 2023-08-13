package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.DevicesModel;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    PlayerStateStorage playerStateStorage;

    @Autowired
    BasicPlayerOperations playerOperations;

    @GetMapping("/state")
    public Mono<PlayerStateDto> currentPlayerState(AuthenticatedUser user) {

        return playerOperations.currentState(User.of(user.getDetails().getId()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(PlayerController::convertToPlayerStateDto);
    }

    @PutMapping("/shuffle")
    public Mono<ResponseEntity<?>> changeShuffleState(AuthenticatedUser user, @RequestParam("state") boolean state) {

        return playerOperations.changeShuffle(User.of(user.getDetails().getId()), state)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState -> ResponseEntity.noContent().build());
    }

    private static PlayerStateDto convertToPlayerStateDto(CurrentPlayerState state) {
        return PlayerStateDto.builder()
                .currentlyPlayingType(state.getPlayingType().name().toLowerCase())
                .isPlaying(state.isPlaying())
                .repeatState(state.getRepeatState())
                .progressMs(state.getProgressMs())
                .devices(toDevicesDto(state.getDevices()))
                .shuffleState(state.getShuffleState())
                .build();
    }

    private static DevicesDto toDevicesDto(DevicesModel devices) {
        List<DeviceDto> dtos = devices.stream().map(PlayerController::toDeviceDto).toList();
        return DevicesDto.builder().devices(dtos).build();
    }

    private static DeviceDto toDeviceDto(DeviceModel device) {
        return DeviceDto.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .volume(device.getVolume())
                .active(device.isActive())
                .build();
    }
}
