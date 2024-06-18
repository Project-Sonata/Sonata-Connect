package com.odeyalo.sonata.connect.model.track;

import com.odeyalo.sonata.connect.model.ArtistList;
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
    ArtistList artists;
    @NotNull
    ImageList images;
    int totalTrackCount;

    @NotNull
    public static Album fromSpec(@NotNull final AlbumSpec spec) {
        return builder()
                .id(spec.getId())
                .name(spec.getName())
                .albumType(spec.getAlbumType())
                .totalTrackCount(spec.getTotalTrackCount())
                .artists(ArtistList.fromSpec(spec.getArtists()))
                .images(ImageList.fromSpec(spec.getImages()))
                .build();
    }
}
