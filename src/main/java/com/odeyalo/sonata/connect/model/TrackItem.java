package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.track.Album;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class TrackItem implements TrackItemSpec {
    @NotNull
    String id;
    @NotNull
    String name;
    @NotNull
    PlayableItemDuration duration;
    @NotNull
    ContextUri contextUri;
    boolean explicit;
    @NotNull
    Order order;
    @NotNull
    ArtistList artists;
    @NotNull
    Album album;

    public static TrackItem fromSpec(@NotNull final TrackItemSpec item) {
        return TrackItem.builder()
                .id(item.getId())
                .name(item.getName())
                .duration(item.getDuration())
                .contextUri(item.getContextUri())
                .explicit(item.isExplicit())
                .order(item.getOrder())
                .artists(ArtistList.fromSpec(item.getArtists()))
                .album(Album.fromSpec(item.getAlbum()))
                .build();
    }
}
