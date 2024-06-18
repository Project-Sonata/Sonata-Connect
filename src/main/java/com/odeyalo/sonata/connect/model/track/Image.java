package com.odeyalo.sonata.connect.model.track;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

@Value
@Builder
public class Image implements ImageSpec {
    @NotNull
    URI url;
    @Nullable
    Integer height;
    @Nullable
    Integer width;

    @NotNull
    public static Image fromSpec(@NotNull final ImageSpec spec) {
        return builder()
                .url(spec.getUrl())
                .height(spec.getHeight())
                .width(spec.getWidth())
                .build();
    }
}
