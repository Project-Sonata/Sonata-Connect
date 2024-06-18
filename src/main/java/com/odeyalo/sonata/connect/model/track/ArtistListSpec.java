package com.odeyalo.sonata.connect.model.track;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * Represents a collection of {@link ArtistSpec} instances that performed on a track or an album.
 *
 * <p><strong>Note:</strong> This collection should always contain at least one artist. </p>
 *
 * @param <T> the type of {@link ArtistSpec} instances contained in this collection
 */
public interface ArtistListSpec<T extends ArtistSpec> {

    /**
     * Retrieves the artist at the specified index.
     *
     * @param index the position of the artist in the collection
     * @return the artist at the specified index, or {@code null} if the index is out of bounds
     */
    @Nullable
    T get(int index);

    /**
     * @return the number of artists in the collection
     */
    int size();

    /**
     * Retrieves the first artist in the collection.
     *
     * @return the first artist in the collection
     */
    @NotNull
    default T getFirst() {
        // we have a requirement that this collection cannot be empty and always contain at least one element
        //noinspection DataFlowIssue
        return get(0);
    }

    /**
     * @return an immutable representation of this collection as {@link ImmutableList}
     */
    @NotNull
    ImmutableList<T> asImmutableList();

    /**
     * @return a {@link Stream} containing all artists in the collection
     */
    @NotNull
    Stream<T> stream();
}
