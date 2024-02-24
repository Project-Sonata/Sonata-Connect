package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.*;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.*;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.ConnectDeviceRequest2DeviceConverter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.CurrentPlayerState2PlayerStateDtoConverter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.Devices2DevicesDtoConverter;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;

@RestController
@RequestMapping("/player")
public class PlayerController {
    private final BasicPlayerOperations playerOperations;
    private final CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter;
    private final Devices2DevicesDtoConverter devicesDtoConverter;
    private final ConnectDeviceRequest2DeviceConverter deviceModelConverter;
    private final Converter<CurrentlyPlayingPlayerState, CurrentlyPlayingPlayerStateDto> currentlyPlayingPlayerStateDtoConverter;

    @Autowired
    public PlayerController(
            BasicPlayerOperations playerOperations,
            CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter,
            Devices2DevicesDtoConverter devicesDtoConverter,
            ConnectDeviceRequest2DeviceConverter deviceModelConverter,
            Converter<CurrentlyPlayingPlayerState, CurrentlyPlayingPlayerStateDto> currentlyPlayingPlayerStateDtoConverter) {
        this.playerOperations = playerOperations;
        this.playerState2PlayerStateDtoConverter = playerState2PlayerStateDtoConverter;
        this.devicesDtoConverter = devicesDtoConverter;
        this.deviceModelConverter = deviceModelConverter;
        this.currentlyPlayingPlayerStateDtoConverter = currentlyPlayingPlayerStateDtoConverter;
    }

    @GetMapping("/state")
    public Mono<PlayerStateDto> currentPlayerState(User user) {

        return playerOperations.currentState(user)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState2PlayerStateDtoConverter::convertTo);
    }

    @GetMapping("/currently-playing")
    public Mono<ResponseEntity<CurrentlyPlayingPlayerStateDto>> currentlyPlaying(User user) {
        return playerOperations.currentlyPlayingState(user)
                .map(state -> ResponseEntity.ok(convertToDto(state)))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/devices")
    public Mono<ResponseEntity<?>> getAvailableDevices(User user) {
        return playerOperations.getDeviceOperations().getConnectedDevices(user)
                .map(this::convertToAvailableDevicesResponseDto)
                .map(body -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body));
    }

    @PutMapping("/play")
    public Mono<ResponseEntity<?>> playOrResume(User user, @RequestBody PlayResumePlaybackRequest body) {
        return playerOperations.playOrResume(user, PlayCommandContext.of(body.getContextUri()), null)
                .thenReturn(default204Response());
    }

    @PutMapping("/shuffle")
    public Mono<ResponseEntity<?>> changeShuffleState(User user,
                                                      @RequestParam("state") boolean state) {

        return playerOperations.changeShuffle(user, state)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState -> default204Response());
    }

    @PutMapping(value = "/device/connect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> addDevice(User user,
                                             @Valid @RequestBody ConnectDeviceRequest body) {
        Device device = deviceModelConverter.convertTo(body);

        return playerOperations.getDeviceOperations().addDevice(user, device)
                .thenReturn(default204Response());
    }

    @PutMapping(value = "/device/switch", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> switchDevices(User user, @RequestBody DeviceSwitchRequest body) {
        return playerOperations.getDeviceOperations().transferPlayback(user,
                        SwitchDeviceCommandArgs.noMatter(),
                        TargetDeactivationDevices.empty(),
                        TargetDevices.of(Arrays.stream(body.getDeviceIds()).map(TargetDevice::of).toList()))
                .thenReturn(default204Response());
    }

    @DeleteMapping(value = "/device")
    public Mono<ResponseEntity<?>> disconnectDevice(@RequestParam("device_id") String deviceId, User user) {
        return playerOperations.getDeviceOperations()
                .disconnectDevice(user, DisconnectDeviceArgs.withDeviceId(deviceId))
                .thenReturn(default204Response());
    }

    private CurrentlyPlayingPlayerStateDto convertToDto(CurrentlyPlayingPlayerState state) {
        return currentlyPlayingPlayerStateDtoConverter.convertTo(state);
    }

    @NotNull
    private AvailableDevicesResponseDto convertToAvailableDevicesResponseDto(Devices devices) {
        DevicesDto devicesDto = devicesDtoConverter.convertTo(devices);
        return AvailableDevicesResponseDto.of(devicesDto);
    }

    @NotNull
    private static ResponseEntity<?> default204Response() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
