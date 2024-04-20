package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Builder
@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerState {
    Long id;
    boolean playing;
    RepeatState repeatState;
    boolean shuffleState;
    Long progressMs = 0L;
    PlayingType playingType;
    DevicesEntity devicesEntity;
    UserEntity user;
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

    public boolean getShuffleState() {
        return shuffleState;
    }

    public PlayingType getCurrentlyPlayingType() {
        return playingType;
    }

    public DevicesEntity getDevicesEntity() {
        return devicesEntity;
    }

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

    public PlayerState pause() {
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
}
