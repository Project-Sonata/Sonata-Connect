package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * Dto representation of {@link PlayerEvent}
 */
@Data
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "event_type")
@JsonSubTypes(value = {
                @JsonSubTypes.Type(value = PlayerStateUpdatedPlayerEventDto.class, name = "PLAYER_STATE_UPDATED"),
                @JsonSubTypes.Type(value = DeviceConnectedPlayerEventDto.class, name = "NEW_DEVICE_CONNECTED"),
                @JsonSubTypes.Type(value = DeviceDisconnectedPlayerEventDto.class, name = "DEVICE_DISAPPEARED")
        }
)
public abstract class PlayerEventDto {

    @JsonProperty("event_type")
    public abstract PlayerEvent.EventType getEventType();
}
