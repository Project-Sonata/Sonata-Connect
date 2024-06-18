package com.odeyalo.sonata.connect.model.track;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface ImageListSpec<T extends ImageSpec> extends Iterable<T> {

    @Nullable
    T get(int index);

    int size();

    boolean isEmpty();

    @NotNull
    ImmutableList<T> asImmutableList();

    @NotNull
    Stream<T> stream();

}
