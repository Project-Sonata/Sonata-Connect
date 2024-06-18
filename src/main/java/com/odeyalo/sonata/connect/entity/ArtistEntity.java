package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Represent an {@link ArtistSpec} that can be persisted to database.
 */
@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class ArtistEntity implements ArtistSpec {
    @NotNull
    ArtistId id;
    @NotNull
    String name;
    @NotNull
    ContextUri contextUri;

    @NotNull
    public static ArtistEntity fromSpec(@NotNull final ArtistSpec spec) {
        return ArtistEntity.builder()
                .id(spec.getId())
                .name(spec.getName())
                .contextUri(spec.getContextUri())
                .build();
    }
}
