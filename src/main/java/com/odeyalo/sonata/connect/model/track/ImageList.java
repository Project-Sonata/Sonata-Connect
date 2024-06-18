package com.odeyalo.sonata.connect.model.track;

import com.google.common.collect.ImmutableList;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Value
@Builder
public class ImageList implements ImageListSpec<Image> {
    @Singular
    @Getter(value = AccessLevel.PRIVATE)
    List<Image> images;

    @NotNull
    public static ImageList empty() {
        return builder().build();
    }

    @NotNull
    public ImageList single(@NotNull final Image image) {
        return builder().image(image).build();
    }

    @NotNull
    public static ImageList fromSpec(@NotNull final ImageListSpec<? extends ImageSpec> spec) {
        final List<Image> images = spec.stream().map(Image::fromSpec).toList();

        return builder().images(images).build();
    }

    @Override
    @Nullable
    public Image get(final int index) {
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

    @NotNull
    @Override
    public ImmutableList<Image> asImmutableList() {
        return ImmutableList.copyOf(images);
    }

    @NotNull
    @Override
    public Stream<Image> stream() {
        return images.stream();
    }

    @NotNull
    @Override
    public Iterator<Image> iterator() {
        return images.iterator();
    }
}
