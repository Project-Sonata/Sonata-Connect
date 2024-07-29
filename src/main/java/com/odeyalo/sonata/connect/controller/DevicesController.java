package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.dto.AvailableDevicesResponseDto;
import com.odeyalo.sonata.connect.dto.DeviceSwitchRequest;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.DeviceOperations;
import com.odeyalo.sonata.connect.service.player.DisconnectDeviceArgs;
import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.dto.Devices2DevicesDtoConverter;
import com.odeyalo.sonata.connect.support.web.HttpStatus;
import com.odeyalo.sonata.connect.support.web.annotation.ConnectionTarget;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> getAvailableDevices(@NotNull final User user) {
        return deviceOperations.getConnectedDevices(user)
                .map(this::convertToAvailableDevicesResponseDto)
                .map(HttpStatus::ok);
    }

    @PutMapping(value = "/device/connect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> addDevice(@NotNull final User user,
                                             @NotNull @ConnectionTarget final Device device) {

        return deviceOperations.addDevice(user, device)
                .thenReturn(HttpStatus.default204Response());
    }

    @PutMapping(value = "/device/switch", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> switchDevices(User user, @RequestBody DeviceSwitchRequest body) {
        return deviceOperations.transferPlayback(
                        user,
                        SwitchDeviceCommandArgs.noMatter(),
                        TargetDeactivationDevices.empty(),
                        TargetDevices.fromDeviceIds(body.getDeviceIds()))
                .thenReturn(HttpStatus.default204Response());
    }

    @DeleteMapping(value = "/device")
    public Mono<ResponseEntity<?>> disconnectDevice(@RequestParam("device_id") String deviceId, User user) {
        return deviceOperations.disconnectDevice(user, DisconnectDeviceArgs.withDeviceId(deviceId))
                .thenReturn(HttpStatus.default204Response());
    }

    @NotNull
    private AvailableDevicesResponseDto convertToAvailableDevicesResponseDto(Devices devices) {
        DevicesDto devicesDto = devicesDtoConverter.convertTo(devices);
        return AvailableDevicesResponseDto.of(devicesDto);
    }
}
