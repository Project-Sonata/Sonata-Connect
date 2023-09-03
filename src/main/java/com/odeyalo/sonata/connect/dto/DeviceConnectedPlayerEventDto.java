package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.connect.service.player.sync.event.DeviceConnectedPlayerEvent;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Dto wrapper for {@link DeviceConnectedPlayerEvent}
 */
@Data
@AllArgsConstructor(staticName = "of")
public class DeviceConnectedPlayerEventDto extends PlayerEventDto {
    @JsonProperty("player_state")
    PlayerStateDto playerState;
    @JsonProperty("device_that_changed")
    String deviceThatChanged;

    @Override
    public PlayerEvent.EventType getEventType() {
        return PlayerEvent.EventType.NEW_DEVICE_CONNECTED;
    }
}
