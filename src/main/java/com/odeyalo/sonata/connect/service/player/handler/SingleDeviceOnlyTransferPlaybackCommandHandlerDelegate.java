package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.Devices;
import com.odeyalo.sonata.connect.entity.InMemoryDevice;
import com.odeyalo.sonata.connect.entity.InMemoryDevices;
import com.odeyalo.sonata.connect.exception.DeviceNotFoundException;
import com.odeyalo.sonata.connect.exception.MultipleTargetDevicesNotSupportedException;
import com.odeyalo.sonata.connect.exception.NeverHappeningException;
import com.odeyalo.sonata.connect.exception.TargetDeviceRequiredException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
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
        Devices connectedDevices = state.getDevices();
        TargetDevice targetDevice = targetDevices.peekFirst();

        if (containsDevice(targetDevice, connectedDevices)) {
            return doTransferPlayback(state, targetDevice, connectedDevices);
        }

        return Mono.error(DeviceNotFoundException.defaultException());
    }

    private Mono<PersistablePlayerState> doTransferPlayback(PersistablePlayerState state, TargetDevice deviceData, Devices connectedDevices) {
        Device deviceToActivate = findDevice(deviceData, connectedDevices);

        Device deactivatedDevice = deativateCurrentlyActiveDevice(connectedDevices);

        Device activatedDevice = activeDevice(deviceToActivate);

        Devices updatedDevices = updateCurrentlyConnectedDevices(connectedDevices, deactivatedDevice, activatedDevice);

        state.setDevices(updatedDevices);

        return playerStateStorage.save(state);
    }

    private static Devices updateCurrentlyConnectedDevices(Devices connectedDevices, Device deactivatedDevice, Device activatedDevice) {

        InMemoryDevices currentDevices = new InMemoryDevices(connectedDevices.getDevices());

        if (deactivatedDevice != null) {
            currentDevices.getDevices().removeIf((device) -> device.getId().equals(deactivatedDevice.getId()));
            currentDevices.addDevice(deactivatedDevice);
        }

        currentDevices.getDevices().removeIf((device) -> device.getId().equals(activatedDevice.getId()));

        currentDevices.addDevice(activatedDevice);

        return currentDevices;
    }

    @NotNull
    private static Device findDevice(TargetDevice targetDevice, Devices devices) {
        return devices.stream().filter(device -> matchesDeviceId(targetDevice, device)).findFirst()
                .orElseThrow(() -> NeverHappeningException.withCustomMessage("Looks like the code does not check the length of the devices before calling this method"));
    }

    @NotNull
    private static InMemoryDevice activeDevice(Device targetDevice) {
        return InMemoryDevice.copy(targetDevice).toBuilder().active(true).build();
    }

    @Nullable
    private static Device deativateCurrentlyActiveDevice(Devices devices) {
        List<Device> activeDevices = devices.getActiveDevices();
        if (activeDevices.isEmpty()) {
            return null;
        }
        return InMemoryDevice.copy(activeDevices.get(0)).toBuilder().active(false).build();
    }

    private static boolean containsDevice(TargetDevice targetDevice, Devices devices) {
        return devices.stream().anyMatch(device -> matchesDeviceId(targetDevice, device));
    }

    private static boolean matchesDeviceId(TargetDevice targetDevice, Device device) {
        return device.getId().equals(targetDevice.getId());
    }
}