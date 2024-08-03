package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevice;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.support.time.Clock;
import com.odeyalo.sonata.connect.support.time.JavaClock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
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
    @Builder.Default
    long progressMs = -1L;
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
    @Builder.Default
    long lastPauseTime = 0;
    @Builder.Default
    long playStartTime = 0;
    @NotNull
    @Builder.Default
    Clock clock = JavaClock.instance();

    @NotNull
    public static CurrentPlayerState emptyFor(@NotNull final User user) {
        return builder()
                .id(new Random().nextLong(0, Long.MAX_VALUE))
                .user(user)
                .build();
    }

    @NotNull
    public CurrentPlayerState useClock(@NotNull final Clock clock) {
        return withClock(clock);
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

    public boolean hasDevice(@NotNull final TargetDevice searchTarget) {
        return devices.hasDevice(searchTarget);
    }

    public boolean missingPlayingItem() {
        return playableItem == null;
    }

    public boolean missingActiveDevice() {
        return !hasActiveDevice();
    }

    public boolean hasPlayingItem() {
        return playableItem != null;
    }

    public long getProgressMs() {
        if ( playableItem == null ) {
            return -1L;
        }

        if (playableItem.getDuration().asMilliseconds() <= (progressMs + calculateProgress())) {
            return playableItem.getDuration().asMilliseconds();
        }

        if ( isPlaying() ) {
            return progressMs + calculateProgress();
        }

        return progressMs;
    }

    @NotNull
    public CurrentPlayerState disconnectDevice(@NotNull final String deviceId) {
        final TargetDeactivationDevice deactivationTarget = TargetDeactivationDevice.of(deviceId);
        return disconnectDevice(deactivationTarget);
    }

    @NotNull
    public CurrentPlayerState changeVolume(@NotNull final Volume volume) {

        final Devices devices = this.devices.changeVolume(volume);

        // For performance
        return toBuilder()
                .volume(volume)
                .devices(devices)
                .build();
    }

    @NotNull
    public CurrentPlayerState transferPlayback(@NotNull final TargetDevice deviceToTransferPlayback) {
        final var updatedDevices = devices.transferPlayback(deviceToTransferPlayback);

        return withDevices(updatedDevices);
    }

    @NotNull
    public CurrentPlayerState play(@NotNull final PlayableItem item) {
        return this.toBuilder()
                .playing(true)
                .playableItem(item)
                .playingType(PlayingType.valueOf(item.getItemType().name()))
                .playStartTime(clock.currentTimeMillis())
                .progressMs(0L)
                .build();
    }

    @NotNull
    public CurrentPlayerState resumePlayback() {
        return this.toBuilder()
                .playing(true)
                .playStartTime(clock.currentTimeMillis())
                .build();
    }

    @NotNull
    public CurrentPlayerState pause() {
        if ( isPlaying() ) {
            return this.toBuilder()
                    .playing(false)
                    .lastPauseTime(clock.currentTimeMillis())
                    .progressMs(progressMs + calculateProgress())
                    .build();
        }

        return this;
    }

    private long calculateProgress() {
        if ( isPlaying() ) {
            return clock.currentTimeMillis() - playStartTime;
        } else {
            return lastPauseTime - playStartTime;
        }
    }
}
