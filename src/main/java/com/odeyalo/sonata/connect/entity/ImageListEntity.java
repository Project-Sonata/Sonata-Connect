package com.odeyalo.sonata.connect.entity;

import com.google.common.collect.ImmutableList;
import com.odeyalo.sonata.connect.model.track.ImageListSpec;
import com.odeyalo.sonata.connect.model.track.ImageSpec;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Value
@Builder
public  class ImageListEntity implements ImageListSpec<ImageEntity> {
    @Singular
    @Getter(value = AccessLevel.PRIVATE)
    List<ImageEntity> images;

    @NotNull
    public static ImageListEntity single(@NotNull final ImageEntity image) {
        return builder().image(image).build();
    }

    @NotNull
    public static ImageListEntity fromSpec(@NotNull final ImageListSpec<? extends ImageSpec> spec) {
        final List<ImageEntity> images = spec.stream().map(ImageEntity::fromSpec).toList();

        return builder().images(images).build();
    }

    @Override
    @Nullable
    public ImageEntity get(final int index) {
        if ( index >= size() || index < 0 ) {
            return null;
        }
        return images.get(index);
    }

    @Override
    public int size() {
        return images.size();
    }

    @Override
    public boolean isEmpty() {
        return images.isEmpty();
    }

    @Override
    public @NotNull ImmutableList<ImageEntity> asImmutableList() {
        return ImmutableList.copyOf(images);
    }

    @Override
    public @NotNull Stream<ImageEntity> stream() {
        return images.stream();
    }

    @NotNull
    @Override
    public Iterator<ImageEntity> iterator() {
        return images.iterator();
    }
}
