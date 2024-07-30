package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.ContextUri;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provide info for Play command that should be played now
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class PlayCommandContext {
    @Nullable
    ContextUri contextUri;

    @NotNull
    public static PlayCommandContext resumePlayback() {
        return new PlayCommandContext(null);
    }

    public static PlayCommandContext from(@NotNull ContextUri contextUri) {
        return of(contextUri);
    }
}
