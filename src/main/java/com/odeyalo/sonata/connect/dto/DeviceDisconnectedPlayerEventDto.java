package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Wrapper for {@link com.odeyalo.sonata.connect.service.player.sync.event.DeviceDisconnectedPlayerEvent}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor(staticName = "of")
public class DeviceDisconnectedPlayerEventDto extends PlayerEventDto {
    @JsonProperty("player_state")
    PlayerStateDto playerState;
    @JsonProperty("device_that_changed")
    String deviceThatChanged;

    @Override
    public PlayerEvent.EventType getEventType() {
        return PlayerEvent.EventType.DEVICE_DISAPPEARED;
    }
}
