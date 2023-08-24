package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.*;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @GetMapping("/currently-playing")
    public Mono<ResponseEntity<CurrentlyPlayingPlayerStateDto>> currentlyPlaying(AuthenticatedUser user) {
        return playerOperations.currentlyPlayingState(User.of(user.getDetails().getId()))
                .map(state -> ResponseEntity.ok(convertToDto(state)))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/devices")
    public Mono<ResponseEntity<?>> getAvailableDevices(AuthenticatedUser user) {
        return playerOperations.getDeviceOperations()
                .getConnectedDevices(User.of(user.getDetails().getId()))
                .map(devices -> AvailableDevicesResponseDto.of(toDevicesDto(devices)))
                .map(body -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body));
    }

    private static CurrentlyPlayingPlayerStateDto convertToDto(CurrentlyPlayingPlayerState state) {
        return CurrentlyPlayingPlayerStateDto.of(state.getShuffleState());
    }

    @PutMapping("/shuffle")
    public Mono<ResponseEntity<?>> changeShuffleState(AuthenticatedUser user,
                                                      @RequestParam("state") boolean state) {

        return playerOperations.changeShuffle(User.of(user.getDetails().getId()), state)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState -> ResponseEntity.noContent().build());
    }


    @PutMapping(value = "/device/connect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> addDevice(AuthenticatedUser authenticatedUser, @RequestBody ConnectDeviceRequest body) {
        return playerOperations.getDeviceOperations().addDevice(User.of(authenticatedUser.getDetails().getId()), convertToDeviceModel(body))
                .thenReturn(ResponseEntity.noContent()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build());
    }

    private static DeviceModel convertToDeviceModel(ConnectDeviceRequest body) {
        return DeviceModel.of(body.getId(), body.getName(), body.getDeviceType(), body.getVolume(), true);
    }

    private static PlayerStateDto convertToPlayerStateDto(CurrentPlayerState state) {
        return PlayerStateDto.builder()
                .currentlyPlayingType(playingTypeOrNull(state))
                .isPlaying(state.isPlaying())
                .repeatState(state.getRepeatState())
                .progressMs(state.getProgressMs())
                .devices(toDevicesDto(state.getDevices()))
                .shuffleState(state.getShuffleState())
                .build();
    }

    private static String playingTypeOrNull(CurrentPlayerState state) {

        PlayingType playingType = state.getPlayingType();

        return playingType != null ? playingType.name().toLowerCase() : null;
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
