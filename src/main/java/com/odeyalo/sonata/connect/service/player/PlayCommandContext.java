package com.odeyalo.sonata.connect.service.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Provide info for Play command that should be played now
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class PlayCommandContext {
    // Context URI to start play, supported types are: track, playlist, album, artist.
    String contextUri;

    public static PlayCommandContext from(@NotNull String contextUri) {
        return of(contextUri);
    }
}
