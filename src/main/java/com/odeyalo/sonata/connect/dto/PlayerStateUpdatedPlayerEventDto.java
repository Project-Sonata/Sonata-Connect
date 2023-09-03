package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto wrapper for PlayerStateUpdatedPlayerEvent
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlayerStateUpdatedPlayerEventDto extends PlayerEventDto {
    @JsonProperty("player_state")
    PlayerStateDto playerState;
    @JsonProperty("event_type")
    PlayerEvent.EventType eventType;
    @JsonProperty("device_that_changed")
    String deviceThatChanged;
}
