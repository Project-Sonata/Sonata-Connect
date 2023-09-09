package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.entity.*;
import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.InMemoryDeviceEntity;
import com.odeyalo.sonata.connect.exception.*;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PersistableDevicesEntity;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.PersistablePlayerState2CurrentPlayerStateConverter;
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
    private final PlayerStateStorage playerStateStorage;
    private final PersistablePlayerState2CurrentPlayerStateConverter playerStateConverterSupport;

    public SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate(PlayerStateStorage playerStateStorage,
                                                                  PersistablePlayerState2CurrentPlayerStateConverter playerStateConverterSupport) {
        this.playerStateStorage = playerStateStorage;
        this.playerStateConverterSupport = playerStateConverterSupport;
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> transferPlayback(User user, SwitchDeviceCommandArgs args, TargetDeactivationDevices deactivationDevices, TargetDevices targetDevices) {
        if (deactivationDevices.size() > 1) {
            return Mono.error(SingleTargetDeactivationDeviceRequiredException.defaultException());
        }
        if (targetDevices.size() < 1) {
            return Mono.error(TargetDeviceRequiredException.defaultException());
        }
        if (targetDevices.size() > 1) {
            return Mono.error(MultipleTargetDevicesNotSupportedException.defaultException());
        }
        return playerStateStorage.findByUserId(user.getId())
                .flatMap(state -> delegateTransferPlayback(targetDevices, state))
                .map(playerStateConverterSupport::convertTo);
    }

    @NotNull
    private Mono<PersistablePlayerState> delegateTransferPlayback(TargetDevices targetDevices, PersistablePlayerState state) {
        DevicesEntity connectedDevicesEntity = state.getDevicesEntity();
        TargetDevice targetDevice = targetDevices.peekFirst();

        if (containsDevice(targetDevice, connectedDevicesEntity)) {
            return doTransferPlayback(state, targetDevice, connectedDevicesEntity);
        }

        return Mono.error(DeviceNotFoundException.defaultException());
    }

    private Mono<PersistablePlayerState> doTransferPlayback(PersistablePlayerState state, TargetDevice deviceData, DevicesEntity connectedDevicesEntity) {
        DeviceEntity activatedDeviceEntity = findAndActivate(deviceData, connectedDevicesEntity);

        DeviceEntity deactivatedDeviceEntity = deativateCurrentlyActiveDevice(connectedDevicesEntity);

        DevicesEntity updatedDevicesEntity = updateCurrentlyConnectedDevices(connectedDevicesEntity, activatedDeviceEntity, deactivatedDeviceEntity);

        state.setDevicesEntity(updatedDevicesEntity);

        return playerStateStorage.save(state);
    }

    @NotNull
    private static DeviceEntity findAndActivate(TargetDevice deviceData, DevicesEntity connectedDevicesEntity) {
        DeviceEntity deviceEntityToActivate = findDevice(deviceData, connectedDevicesEntity);
        return activateDevice(deviceEntityToActivate);
    }

    private static DevicesEntity updateCurrentlyConnectedDevices(DevicesEntity connectedDevicesEntity, DeviceEntity activatedDeviceEntity, DeviceEntity deactivatedDeviceEntity) {
        PersistableDevicesEntity currentDevices = PersistableDevicesEntity.copyFrom(connectedDevicesEntity);

        if (deactivatedDeviceEntity != null) {
            currentDevices.removeIf((device) -> device.getId().equals(deactivatedDeviceEntity.getId()));
            currentDevices.addDevice(deactivatedDeviceEntity);
        }

        currentDevices.removeIf((device) -> device.getId().equals(activatedDeviceEntity.getId()));

        currentDevices.addDevice(activatedDeviceEntity);

        return currentDevices;
    }

    @NotNull
    private static DeviceEntity findDevice(TargetDevice targetDevice, DevicesEntity devicesEntity) {
        return devicesEntity.stream().filter(device -> matchesDeviceId(targetDevice, device)).findFirst()
                .orElseThrow(() -> NeverHappeningException.withCustomMessage("Looks like the code does not check the length of the devices before calling this method"));
    }

    @NotNull
    private static InMemoryDeviceEntity activateDevice(DeviceEntity targetDeviceEntity) {
        return InMemoryDeviceEntity.copy(targetDeviceEntity).toBuilder().active(true).build();
    }

    @Nullable
    private static DeviceEntity deativateCurrentlyActiveDevice(DevicesEntity devicesEntity) {
        List<DeviceEntity> activeDeviceEntities = devicesEntity.getActiveDevices();
        if (activeDeviceEntities.isEmpty()) {
            return null;
        }
        return InMemoryDeviceEntity.copy(activeDeviceEntities.get(0)).toBuilder().active(false).build();
    }

    private static boolean containsDevice(TargetDevice targetDevice, DevicesEntity devicesEntity) {
        return devicesEntity.stream().anyMatch(device -> matchesDeviceId(targetDevice, device));
    }

    private static boolean matchesDeviceId(TargetDevice targetDevice, DeviceEntity deviceEntity) {
        return deviceEntity.getId().equals(targetDevice.getId());
    }
}