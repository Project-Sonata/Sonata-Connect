package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.track.AlbumSpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class AlbumEntity implements AlbumSpec {
    @NotNull
    AlbumId id;

    @NotNull
    public static AlbumEntity fromSpec(@NotNull final AlbumSpec spec) {
        return builder()
                .id(spec.getId())
                .build();
    }
}
