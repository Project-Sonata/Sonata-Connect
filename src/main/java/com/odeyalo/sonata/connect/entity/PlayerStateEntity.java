package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.ShuffleMode;
import com.odeyalo.sonata.connect.model.Volume;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Builder
@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStateEntity {
    Long id;
    boolean playing;
    @NotNull
    @Builder.Default
    RepeatState repeatState = RepeatState.OFF;
    @NotNull
    @Builder.Default
    ShuffleMode shuffleState = ShuffleMode.OFF;
    @Builder.Default
    Long progressMs = 0L;
    @Nullable
    PlayingType playingType;
    @NotNull
    @Builder.Default
    DevicesEntity devicesEntity = DevicesEntity.empty();
    @NotNull
    UserEntity user;
    @Nullable
    PlayableItemEntity currentlyPlayingItem;
    @NotNull
    Volume volume;
    long playStartTime = 0;
    long lastPauseTime = 0;

    @NotNull
    public ShuffleMode getShuffleState() {
        return shuffleState;
    }

    @Nullable
    public PlayingType getCurrentlyPlayingType() {
        return playingType;
    }

    @Nullable
    public PlayableItemEntity getCurrentlyPlayingItem() {
        return currentlyPlayingItem;
    }

    @NotNull
    public DevicesEntity getDevices() {
        return devicesEntity;
    }
}
