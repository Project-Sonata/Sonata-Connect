package com.odeyalo.sonata.connect.service.player.sync.event;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import static com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent.EventType.PLAYER_STATE_UPDATED;

/**
 * Event that can be triggered when player state has been updated(time has been changed, shuffle or repeat mode has been changed, etc.)
 */
@Value
@AllArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
@Builder
public class PlayerStateUpdatedPlayerEvent implements PlayerEvent {
    CurrentPlayerState playerState;
    EventType eventType;
    String deviceThatChanged;

    public static PlayerStateUpdatedPlayerEvent of(CurrentPlayerState playerState, String deviceThatChanged) {
        return of(playerState, PLAYER_STATE_UPDATED, deviceThatChanged);
    }

    @NotNull
    @Override
    public CurrentPlayerState getCurrentPlayerState() {
        return playerState;
    }

    @NotNull
    @Override
    public EventType getEventType() {
        return eventType;
    }

    @NotNull
    @Override
    public String getDeviceThatChanged() {
        return deviceThatChanged;
    }
}
