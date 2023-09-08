package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Support interface that used as delegate to handle transfer command
 */
public interface TransferPlaybackCommandHandlerDelegate {
    /**
     * Transfer the playback to given devices
     * @param user - user that owns player state
     * @param args - arguments for this command
     * @param deactivationDevices - devices that should be deactivated
     * @param targetDevices - devices to transfer playback.
     * @return - Mono with updated player state
     *
     * @see BasicPlayerOperations#getDeviceOperations()#transferPlayback(User, SwitchDeviceCommandArgs, TargetDeactivationDevices, TargetDevices)
     */
    @NotNull
    Mono<CurrentPlayerState> transferPlayback(User user, SwitchDeviceCommandArgs args, TargetDeactivationDevices deactivationDevices, TargetDevices targetDevices);
}