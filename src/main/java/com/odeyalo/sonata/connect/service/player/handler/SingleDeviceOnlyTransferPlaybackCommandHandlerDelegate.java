package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
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
    private Mono<PlayerStateEntity> delegateTransferPlayback(TargetDevices targetDevices, PlayerStateEntity state) {
        DevicesEntity connectedDevicesEntity = state.getDevicesEntity();
        TargetDevice targetDevice = targetDevices.peekFirst();

        if ( containsTargetDevice(targetDevice, connectedDevicesEntity) ) {
            return doTransferPlayback(state, targetDevice, connectedDevicesEntity);
        }

        return Mono.error(DeviceNotFoundException.defaultException());
    }

    private Mono<PlayerStateEntity> doTransferPlayback(PlayerStateEntity state,
                                                       TargetDevice deviceToTransferPlayback,
                                                       DevicesEntity connectedDevicesEntity) {

        DevicesEntity updatedDevicesEntity = updateCurrentlyConnectedDevices(connectedDevicesEntity, deviceToTransferPlayback);

        state.setDevicesEntity(updatedDevicesEntity);

        return playerStateRepository.save(state);
    }

    private static DevicesEntity updateCurrentlyConnectedDevices(DevicesEntity connectedDevicesContainer,
                                                                 TargetDevice deviceToTransferPlayback) {

        DeviceEntity currentlyActiveDevice = findCurrentlyActiveDevice(connectedDevicesContainer);

        DeviceEntity deviceToActivate = findDeviceToActivate(deviceToTransferPlayback, connectedDevicesContainer);

        if ( currentlyActiveDevice != null ) {
            connectedDevicesContainer.deactivateDevice(currentlyActiveDevice);
        }

        connectedDevicesContainer.activateDevice(deviceToActivate);

        return connectedDevicesContainer;
    }

    @NotNull
    private static DeviceEntity findDeviceToActivate(TargetDevice searchTarget, DevicesEntity devicesEntity) {
        return devicesEntity.findById(searchTarget.getId())
                .orElseThrow(() -> NeverHappeningException.withCustomMessage("Looks like the code does not check the length of the devices before calling this method"));
    }

    @Nullable
    private static DeviceEntity findCurrentlyActiveDevice(DevicesEntity devicesEntity) {
        List<DeviceEntity> activeDeviceEntities = devicesEntity.getActiveDevices();

        if ( activeDeviceEntities.isEmpty() ) {
            // We do not have the active device, return null and skip it
            return null;
        }

        return activeDeviceEntities.get(0);
    }

    private static boolean containsTargetDevice(TargetDevice targetDevice, DevicesEntity deviceContainer) {
        return deviceContainer.containsById(targetDevice.getId());
    }
}