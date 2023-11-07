package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.exception.*;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * TransferPlaybackCommandHandlerDelegate that supports only single active device at once
 */
@Component
public class SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate implements TransferPlaybackCommandHandlerDelegate {
    private final PlayerStateRepository playerStateRepository;
    private final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport;

    public SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate(PlayerStateRepository playerStateRepository,
                                                                  PlayerState2CurrentPlayerStateConverter playerStateConverterSupport) {
        this.playerStateRepository = playerStateRepository;
        this.playerStateConverterSupport = playerStateConverterSupport;
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> transferPlayback(User user, SwitchDeviceCommandArgs args, TargetDeactivationDevices deactivationDevices, TargetDevices targetDevices) {
        if ( deactivationDevices.size() > 1 ) {
            return Mono.error(SingleTargetDeactivationDeviceRequiredException.defaultException());
        }
        if ( targetDevices.size() < 1 ) {
            return Mono.error(TargetDeviceRequiredException.defaultException());
        }
        if ( targetDevices.size() > 1 ) {
            return Mono.error(MultipleTargetDevicesNotSupportedException.defaultException());
        }
        return playerStateRepository.findByUserId(user.getId())
                .flatMap(state -> delegateTransferPlayback(targetDevices, state))
                .map(playerStateConverterSupport::convertTo);
    }

    @NotNull
    private Mono<PlayerState> delegateTransferPlayback(TargetDevices targetDevices, PlayerState state) {
        DevicesEntity connectedDevicesEntity = state.getDevicesEntity();
        TargetDevice targetDevice = targetDevices.peekFirst();

        if ( containsDevice(targetDevice, connectedDevicesEntity) ) {
            return doTransferPlayback(state, targetDevice, connectedDevicesEntity);
        }

        return Mono.error(DeviceNotFoundException.defaultException());
    }

    private Mono<PlayerState> doTransferPlayback(PlayerState state, TargetDevice deviceData, DevicesEntity connectedDevicesEntity) {
        DeviceEntity activatedDeviceEntity = findAndActivate(deviceData, connectedDevicesEntity);

        DeviceEntity deactivatedDeviceEntity = deactivateCurrentlyActiveDevice(connectedDevicesEntity);

        DevicesEntity updatedDevicesEntity = updateCurrentlyConnectedDevices(connectedDevicesEntity, activatedDeviceEntity, deactivatedDeviceEntity);

        state.setDevicesEntity(updatedDevicesEntity);

        return playerStateRepository.save(state);
    }

    @NotNull
    private static DeviceEntity findAndActivate(TargetDevice deviceData, DevicesEntity connectedDevicesEntity) {
        DeviceEntity deviceEntityToActivate = findDevice(deviceData, connectedDevicesEntity);
        return activateDevice(deviceEntityToActivate);
    }

    private static DevicesEntity updateCurrentlyConnectedDevices(DevicesEntity connectedDevicesEntity,
                                                                 DeviceEntity activatedDeviceEntity,
                                                                 DeviceEntity deactivatedDeviceEntity) {
        DevicesEntity currentDevices = DevicesEntity.copyFrom(connectedDevicesEntity);

        if ( deactivatedDeviceEntity != null ) {
            currentDevices.removeIf((device) -> device.getId().equals(deactivatedDeviceEntity.getId()));
            currentDevices.addDevice(deactivatedDeviceEntity);
        }

        currentDevices.removeIf((device) -> device.getId().equals(activatedDeviceEntity.getId()));

        currentDevices.addDevice(activatedDeviceEntity);

        return currentDevices;
    }

    @NotNull
    private static DeviceEntity findDevice(TargetDevice searchTarget, DevicesEntity devicesEntity) {
        return devicesEntity.stream()
                .filter(device -> matchesDeviceId(searchTarget, device))
                .findFirst()
                .orElseThrow(() -> NeverHappeningException.withCustomMessage("Looks like the code does not check the length of the devices before calling this method"));
    }

    @NotNull
    private static DeviceEntity activateDevice(DeviceEntity targetDeviceEntity) {
        return DeviceEntity.copy(targetDeviceEntity).toBuilder().active(true).build();
    }

    @Nullable
    private static DeviceEntity deactivateCurrentlyActiveDevice(DevicesEntity devicesEntity) {
        List<DeviceEntity> activeDeviceEntities = devicesEntity.getActiveDevices();
        if ( activeDeviceEntities.isEmpty() ) {
            return null;
        }
        DeviceEntity currentlyActiveDevice = activeDeviceEntities.get(0);
        return DeviceEntity.copy(currentlyActiveDevice).toBuilder().active(false).build();
    }

    private static boolean containsDevice(TargetDevice targetDevice, DevicesEntity devicesEntity) {
        return devicesEntity.stream().anyMatch(device -> matchesDeviceId(targetDevice, device));
    }

    private static boolean matchesDeviceId(TargetDevice targetDevice, DeviceEntity deviceEntity) {
        return deviceEntity.getId().equals(targetDevice.getId());
    }
}