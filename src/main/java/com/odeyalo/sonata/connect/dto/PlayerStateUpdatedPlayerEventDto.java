package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Dto wrapper for PlayerStateUpdatedPlayerEvent
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Data
public class PlayerStateUpdatedPlayerEventDto extends PlayerEventDto {
    @JsonProperty("player_state")
    PlayerStateDto playerState;
    @JsonProperty("device_that_changed")
    String deviceThatChanged;

    @Override
    public PlayerEvent.EventType getEventType() {
        return PlayerEvent.EventType.PLAYER_STATE_UPDATED;
    }
}
