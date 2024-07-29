package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.*;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.service.player.*;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.dto.ConnectDeviceRequest2DeviceConverter;
import com.odeyalo.sonata.connect.service.support.mapper.dto.Devices2DevicesDtoConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class DevicesController {
    private final DeviceOperations deviceOperations;
    private final Devices2DevicesDtoConverter devicesDtoConverter;
    private final ConnectDeviceRequest2DeviceConverter deviceModelConverter;

    @GetMapping("/devices")
    public Mono<ResponseEntity<?>> getAvailableDevices(User user) {
        return deviceOperations.getConnectedDevices(user)
                .map(this::convertToAvailableDevicesResponseDto)
                .map(body -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body));
    }

    @PutMapping(value = "/device/connect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> addDevice(User user,
                                             @Valid @RequestBody ConnectDeviceRequest body) {
        Device device = deviceModelConverter.convertTo(body);

        return deviceOperations.addDevice(user, device)
                .thenReturn(default204Response());
    }

    @PutMapping(value = "/device/switch", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> switchDevices(User user, @RequestBody DeviceSwitchRequest body) {
        return deviceOperations.transferPlayback(
                        user,
                        SwitchDeviceCommandArgs.noMatter(),
                        TargetDeactivationDevices.empty(),
                        TargetDevices.fromDeviceIds(body.getDeviceIds()))
                .thenReturn(default204Response());
    }

    @DeleteMapping(value = "/device")
    public Mono<ResponseEntity<?>> disconnectDevice(@RequestParam("device_id") String deviceId, User user) {
        return deviceOperations.disconnectDevice(user, DisconnectDeviceArgs.withDeviceId(deviceId))
                .thenReturn(default204Response());
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
