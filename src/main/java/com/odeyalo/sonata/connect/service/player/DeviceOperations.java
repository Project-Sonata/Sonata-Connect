package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.DevicesModel;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Interface to handle basic operations for connected device
 */
public interface DeviceOperations {

    @NotNull
    Mono<CurrentPlayerState> addDevice(User user, DeviceModel device);

    @NotNull
    Mono<Boolean> containsById(User user, String deviceId);

    /**
     * Transfer the playback to given devices
     * @param user - user that owns player state
     * @param args - arguments for this command
     * @param deactivationDevices - devices that should be deactivated
     * @param targetDevices - devices to transfer playback.
     * @return - Mono with updated player state
     */
    @NotNull
    Mono<CurrentPlayerState> transferPlayback(User user, SwitchDeviceCommandArgs args, TargetDeactivationDevices deactivationDevices, TargetDevices targetDevices);

    @NotNull
    Mono<DevicesModel> getConnectedDevices(User user);

}
