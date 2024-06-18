package com.odeyalo.sonata.connect.model;

import com.google.common.collect.ImmutableList;
import com.odeyalo.sonata.connect.model.track.ArtistListSpec;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@Value
@Builder
public class ArtistList implements ArtistListSpec<Artist> {
    @Getter(value = AccessLevel.PRIVATE)
    @Singular
    List<Artist> artists;

    @NotNull
    public static ArtistList fromSpec(@NotNull final ArtistListSpec<? extends ArtistSpec> spec) {

        final List<Artist> mappedArtists = spec.stream().map(Artist::fromSpec).toList();

        return ArtistList.builder()
                .artists(mappedArtists)
                .build();
    }

    @NotNull
    public static ArtistList solo(@NotNull final ArtistSpec performer) {
        return ArtistList.builder().artist(Artist.fromSpec(performer)).build();
    }

    @Override
    @Nullable
    public Artist get(final int index) {
        if (index >= size()) {
            return null;
        }
        return artists.get(index);
    }

    @Override
    public int size() {
        return artists.size();
    }

    @Override
    @NotNull
    public ImmutableList<Artist> asImmutableList() {
        return ImmutableList.copyOf(artists);
    }

    @Override
    @NotNull
    public Stream<Artist> stream() {
        return artists.stream();
    }
}
