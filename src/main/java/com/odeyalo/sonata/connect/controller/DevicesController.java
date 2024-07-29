package com.odeyalo.sonata.connect.controller;

import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.DeviceOperations;
import com.odeyalo.sonata.connect.service.player.DisconnectDeviceArgs;
import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.dto.AvailableDevicesResponseDtoConverter;
import com.odeyalo.sonata.connect.support.web.HttpStatus;
import com.odeyalo.sonata.connect.support.web.annotation.ConnectionTarget;
import com.odeyalo.sonata.connect.support.web.annotation.TransferPlaybackTargets;
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
    private final AvailableDevicesResponseDtoConverter availableDevicesConverter;

    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> getAvailableDevices(@NotNull final User user) {

        return deviceOperations.getConnectedDevices(user)
                .map(availableDevicesConverter::convertTo)
                .map(HttpStatus::ok);
    }

    @PostMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> addDevice(@NotNull final User user,
                                             @NotNull @ConnectionTarget final Device device) {

        return deviceOperations.addDevice(user, device)
                .thenReturn(HttpStatus.default204Response());
    }

    @PutMapping(value = "/device/switch", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> switchDevices(@NotNull final User user,
                                                 @NotNull final SwitchDeviceCommandArgs switchDeviceCommandArgs,
                                                 @NotNull final TargetDeactivationDevices targetDeactivationDevices,
                                                 @NotNull @TransferPlaybackTargets final TargetDevices transferPlaybackTargets) {
        return deviceOperations.transferPlayback(
                        user,
                        switchDeviceCommandArgs,
                        targetDeactivationDevices,
                        transferPlaybackTargets)
                .thenReturn(HttpStatus.default204Response());
    }

    @DeleteMapping(value = "/device", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> disconnectDevice(@NotNull final DisconnectDeviceArgs disconnectDeviceArgs,
                                                    @NotNull final User user) {
        return deviceOperations.disconnectDevice(user, disconnectDeviceArgs)
                .thenReturn(HttpStatus.default204Response());
    }
}
