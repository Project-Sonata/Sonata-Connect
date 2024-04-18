package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

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
    Long progressMs;
    PlayingType playingType;
    DevicesEntity devicesEntity;
    UserEntity user;
    // TODO: why is this a ENTITY, it should be PlayableItem
    PlayableItemEntity currentlyPlayingItem;
    int volume;
    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PRIVATE)
    Instant lastInteractionPlayPauseTime;

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
            progressMs = Instant.now().minusSeconds(lastInteractionPlayPauseTime.getEpochSecond()).getEpochSecond();
        }
        return progressMs;
    }

    public void playOrResume(PlayableItemEntity item) {
        setCurrentlyPlayingItem(item);
        setPlayingType(PlayingType.valueOf(item.getType().name()));
        setPlaying(true);
        lastInteractionPlayPauseTime = Instant.now();
    }

    public PlayerState pause() {
        setPlaying(false);
        lastInteractionPlayPauseTime = Instant.now();
        return this;
    }
}
