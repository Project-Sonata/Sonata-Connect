package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.ShuffleMode;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Builder
@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStateEntity {
    Long id;
    boolean playing;
    @Nullable
    RepeatState repeatState;
    @NotNull
    ShuffleMode shuffleState;
    @Builder.Default
    Long progressMs = 0L;
    @Nullable
    PlayingType playingType;
    DevicesEntity devicesEntity;
    @NotNull
    UserEntity user;
    @Nullable
    PlayableItemEntity currentlyPlayingItem;
    int volume;
    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PRIVATE)
    long playStartTime = 0;
    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PRIVATE)
    long lastPauseTime = 0;

    public static final boolean SHUFFLE_ENABLED = true;
    public static final boolean SHUFFLE_DISABLED = false;

    public ShuffleMode getShuffleState() {
        return shuffleState;
    }

    public PlayingType getCurrentlyPlayingType() {
        return playingType;
    }

    public DevicesEntity getDevicesEntity() {
        return devicesEntity;
    }

    @Nullable
    public PlayableItemEntity getCurrentlyPlayingItem() {
        return currentlyPlayingItem;
    }

    public DevicesEntity getDevices() {
        return devicesEntity;
    }

    public Long getProgressMs() {
        if (currentlyPlayingItem == null) {
            return -1L;
        }

        if ( isPlaying() ) {
            return progressMs + calculateProgress();
        }
        return progressMs;

    }

    private Long calculateProgress() {
        if ( isPlaying() ) {
            return System.currentTimeMillis() - playStartTime;
        } else {
            return lastPauseTime - playStartTime;
        }
    }

    public void playOrResume(PlayableItemEntity item) {
        if ( currentlyPlayingItem == null || isPaused() ) {
            setCurrentlyPlayingItem(item);
            setPlayingType(PlayingType.valueOf(item.getType().name()));
            setPlaying(true);
            playStartTime = System.currentTimeMillis();
            return;
        }

        if ( !Objects.equals(item.getId(), currentlyPlayingItem.getId()) ) {
            setCurrentlyPlayingItem(item);
            setPlayingType(PlayingType.valueOf(item.getType().name()));
            setPlaying(true);
            playStartTime = System.currentTimeMillis();
            progressMs = 0L;
        }
    }

    public PlayerStateEntity pause() {
        if ( isPlaying() ) {
            setPlaying(false);
            lastPauseTime = System.currentTimeMillis();
            progressMs += calculateProgress();
        }
        return this;
    }

    public boolean isPaused() {
        return !isPlaying();
    }

    public boolean hasActiveDevice() {
        return getDevicesEntity().hasActiveDevice();
    }
}
