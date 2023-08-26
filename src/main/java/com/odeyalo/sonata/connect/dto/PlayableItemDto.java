package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.odeyalo.sonata.connect.model.PlayableItemType;
import com.odeyalo.sonata.connect.model.PlayingType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(
        @JsonSubTypes.Type(value = TrackItemDto.class, name = "TRACK")
)
public abstract class PlayableItemDto {
    String id; // ID of the track, episode, podcast, etc
    String uri; // Always null since Sonata does not support it yet.

    @JsonProperty("type")
    public abstract PlayableItemType getPlayingType();
}
