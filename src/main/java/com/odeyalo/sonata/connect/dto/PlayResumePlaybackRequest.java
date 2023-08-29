package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO request to play or resume playback
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayResumePlaybackRequest {
    // Context URI to load tracks, episodes, podcast and start playing it
    @JsonProperty("context_uri")
    String contextUri;
}
