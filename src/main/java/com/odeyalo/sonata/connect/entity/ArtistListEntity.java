package com.odeyalo.sonata.connect.entity;

import com.google.common.collect.ImmutableList;
import com.odeyalo.sonata.connect.model.track.ArtistListSpec;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

@Value
@Builder
public class ArtistListEntity implements ArtistListSpec<ArtistEntity> {
    @Singular
    @Getter(value = AccessLevel.PRIVATE)
    List<ArtistEntity> artists;

    @NotNull
    public static ArtistListEntity solo(@NotNull final ArtistEntity artist) {
        return builder().artist(artist).build();
    }

    @NotNull
    public static ArtistListEntity fromList(@NotNull final List<ArtistEntity> artists) {
        return new ArtistListEntity(artists);
    }

    @NotNull
    public static ArtistListEntity fromSpec(final ArtistListSpec<? extends ArtistSpec> spec) {
        final List<ArtistEntity> mappedArtists = spec.stream()
                .map(ArtistEntity::fromSpec)
                .toList();

        return fromList(mappedArtists);
    }

    @Override
    public ArtistEntity get(final int index) {
        return artists.get(index);
    }

    @Override
    public int size() {
        return artists.size();
    }

    @Override
    @NotNull
    public ImmutableList<ArtistEntity> asImmutableList() {
        return ImmutableList.copyOf(artists);
    }

    @Override
    @NotNull
    public Stream<ArtistEntity> stream() {
        return artists.stream();
    }
}
