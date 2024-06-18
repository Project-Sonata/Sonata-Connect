package com.odeyalo.sonata.connect.model.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class Album implements AlbumSpec {
    @NotNull
    AlbumId id;
    @NotNull
    String name;
    @NotNull
    AlbumType albumType;

    @NotNull
    public static Album fromSpec(@NotNull final AlbumSpec spec) {
        return builder()
                .id(spec.getId())
                .name(spec.getName())
                .albumType(spec.getAlbumType())
                .build();
    }
}
