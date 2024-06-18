package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Basic model that represent an artist of the {@link TrackItem}
 */
@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class Artist implements ArtistSpec {
    @NotNull
    ArtistId id;
    @NotNull
    String name;
    @NotNull
    ContextUri contextUri;

    @NotNull
    public static Artist fromSpec(@NotNull final ArtistSpec spec) {
        return builder()
                .id(spec.getId())
                .name(spec.getName())
                .contextUri(spec.getContextUri())
                .build();
    }
}
