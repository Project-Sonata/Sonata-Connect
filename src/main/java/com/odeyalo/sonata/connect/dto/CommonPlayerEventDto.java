package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Default DTO that just wraps the elements
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonPlayerEventDto extends PlayerEventDto {
    @JsonProperty("player_state")
    PlayerStateDto playerState;
    @JsonProperty("device_that_changed")
    String deviceThatChanged;
    PlayerEvent.EventType eventType;
}
