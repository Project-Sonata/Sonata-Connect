package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.*;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.DevicesModel;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.support.mapper.dto.ConnectDeviceRequest2DeviceModelConverter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.CurrentPlayerState2PlayerStateDtoConverter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.DevicesModel2DevicesDtoConverter;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/player")
public class PlayerController {
    private final BasicPlayerOperations playerOperations;
    private final CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter;
    private final DevicesModel2DevicesDtoConverter devicesDtoConverter;
    private final ConnectDeviceRequest2DeviceModelConverter deviceModelConverter;

    @Autowired
    public PlayerController(
            BasicPlayerOperations playerOperations,
            CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter,
            DevicesModel2DevicesDtoConverter devicesDtoConverter,
            ConnectDeviceRequest2DeviceModelConverter deviceModelConverter
    ) {
        this.playerOperations = playerOperations;
        this.playerState2PlayerStateDtoConverter = playerState2PlayerStateDtoConverter;
        this.devicesDtoConverter = devicesDtoConverter;
        this.deviceModelConverter = deviceModelConverter;
    }

    @GetMapping("/state")
    public Mono<PlayerStateDto> currentPlayerState(AuthenticatedUser user) {

        return playerOperations.currentState(resolveUser(user))
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState2PlayerStateDtoConverter::convertTo);
    }

    @GetMapping("/currently-playing")
    public Mono<ResponseEntity<CurrentlyPlayingPlayerStateDto>> currentlyPlaying(AuthenticatedUser user) {
        return playerOperations.currentlyPlayingState(resolveUser(user))
                .map(state -> ResponseEntity.ok(convertToDto(state)))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/devices")
    public Mono<ResponseEntity<?>> getAvailableDevices(AuthenticatedUser user) {
        return playerOperations.getDeviceOperations().getConnectedDevices(resolveUser(user))
                .map(this::convertToAvailableDevicesResponseDto)
                .map(body -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body));
    }

    @PutMapping("/play")
    public Mono<ResponseEntity<?>> resumePlayback(@RequestBody PlayResumePlaybackRequest body, AuthenticatedUser user) {
        return playerOperations.playOrResume(resolveUser(user), PlayCommandContext.of(body.getContextUri()), null)
                .thenReturn(default204Response());
    }

    @PutMapping("/shuffle")
    public Mono<ResponseEntity<?>> changeShuffleState(AuthenticatedUser user,
                                                      @RequestParam("state") boolean state) {

        return playerOperations.changeShuffle(resolveUser(user), state)
                .subscribeOn(Schedulers.boundedElastic())
                .map(playerState -> default204Response());
    }

    @PutMapping(value = "/device/connect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> addDevice(AuthenticatedUser authenticatedUser,
                                             @Valid @RequestBody ConnectDeviceRequest body) {
        User user = resolveUser(authenticatedUser);
        DeviceModel device = deviceModelConverter.convertTo(body);

        return playerOperations.getDeviceOperations().addDevice(user, device)
                .thenReturn(default204Response());
    }

    @NotNull
    private AvailableDevicesResponseDto convertToAvailableDevicesResponseDto(DevicesModel devices) {
        DevicesDto devicesDto = devicesDtoConverter.convertTo(devices);
        return AvailableDevicesResponseDto.of(devicesDto);
    }

    @NotNull
    private static ResponseEntity<?> default204Response() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private static CurrentlyPlayingPlayerStateDto convertToDto(CurrentlyPlayingPlayerState state) {
        return CurrentlyPlayingPlayerStateDto.of(state.getShuffleState());
    }

    private static User resolveUser(AuthenticatedUser user) {
        return User.of(user.getDetails().getId());
    }
}
