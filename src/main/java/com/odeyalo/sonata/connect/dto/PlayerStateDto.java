package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.odeyalo.sonata.connect.model.RepeatState;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStateDto {
    // Indicates if player plays something
    @JsonProperty("is_playing")
    boolean isPlaying;
    @JsonProperty("repeat_state")
    RepeatState repeatState = RepeatState.OFF;
    @JsonProperty("shuffle_state")
    boolean shuffleState;
    @JsonProperty("currently_playing_type")
    String currentlyPlayingType;
    @JsonProperty("progress_ms")
    private long progressMs;
    @JsonUnwrapped
    private DevicesDto devices;

    public boolean getShuffleState() {
        return shuffleState;
    }
}
