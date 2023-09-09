package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Dto to return current playing state, if any
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CurrentlyPlayingPlayerStateDto {
    @JsonProperty("shuffle_state")
    Boolean shuffleState;
    @JsonProperty("is_playing")
    boolean playing;
    @JsonProperty("currently_playing_type")
    PlayingType currentlyPlayingType;
    @JsonProperty("repeat_state")
    RepeatState repeatState;
    @JsonProperty("playing_item")
    PlayableItemDto currentlyPlayingItem;
    @JsonProperty("devices")
    @JsonUnwrapped
    DevicesDto devices;
}
