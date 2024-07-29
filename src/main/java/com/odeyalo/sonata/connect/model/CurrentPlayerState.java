package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevice;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Represent a current state pf the player despite the fact is playing something or not
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
@With
public class CurrentPlayerState {
    long id;
    boolean playing;
    @NotNull
    @Builder.Default
    RepeatState repeatState = RepeatState.OFF;
    @NotNull
    @Builder.Default
    ShuffleMode shuffleState = ShuffleMode.OFF;
    @Nullable
    Long progressMs;
    @Nullable
    PlayingType playingType;
    @NotNull
    @Builder.Default
    Devices devices = Devices.empty();
    @Nullable
    PlayableItem playableItem;
    @NotNull
    User user;
    @NotNull
    @Builder.Default
    Volume volume = Volume.muted();
    long lastPauseTime = 0;
    long playStartTime = 0;

    @NotNull
    public static CurrentPlayerState emptyFor(@NotNull final User user) {
        return builder()
                .id(new Random().nextLong(0, Long.MAX_VALUE))
                .user(user)
                .build();
    }

    @NotNull
    public ShuffleMode getShuffleState() {
        return shuffleState;
    }

    @Nullable
    public PlayableItem getPlayingItem() {
        return playableItem;
    }

    @NotNull
    public CurrentPlayerState connectDevice(@NotNull final Device device) {
        final Devices newDevices = getDevices().connectDevice(device);
        return withDevices(newDevices);
    }

    @NotNull
    public CurrentPlayerState disconnectDevice(@NotNull final TargetDeactivationDevice deactivationTarget) {
        final Devices updatedDevices = getDevices().disconnectDevice(deactivationTarget);

        return withDevices(updatedDevices);
    }

    public boolean hasActiveDevice() {
        return getDevices().hasActiveDevice();
    }

    @NotNull
    public CurrentPlayerState disconnectDevice(@NotNull final String deviceId) {
        final TargetDeactivationDevice deactivationTarget = TargetDeactivationDevice.of(deviceId);
        return disconnectDevice(deactivationTarget);
    }

    @NotNull
    public CurrentPlayerState changeVolume(final Volume volume) {

        final Devices devices = this.devices.changeVolume(volume);

        // For performance
        return toBuilder()
                .volume(volume)
                .devices(devices)
                .build();
    }
}
