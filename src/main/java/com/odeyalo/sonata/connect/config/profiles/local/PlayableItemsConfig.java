package com.odeyalo.sonata.connect.config.profiles.local;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.model.track.*;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.PredefinedPlayableItemLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.util.List;

@Configuration
@Profile("local")
public class PlayableItemsConfig {
    private final Logger logger = LoggerFactory.getLogger(PlayableItemsConfig.class);

    @Bean
    public PlayableItemLoader playableItemLoader() {
        ArtistList artists = ArtistList.solo(
                Artist.of(ArtistSpec.ArtistId.of("fea3aFas3f"), "Alex G", ContextUri.forArtist("fea3aFas3f"))
        );

        ImageList images = ImageList.builder()
                .image(Image.builder().url(URI.create("https://i.pinimg.com/564x/02/27/b0/0227b0ff5ff93d6429d2c80d402cea43.jpg")).build())
                .image(Image.builder().url(URI.create("https://i.pinimg.com/564x/db/ff/9f/dbff9f74ef082687010dacc455eac7ac.jpg")).build())
                .build();

        Album albumInfo = Album.builder()
                .id(AlbumSpec.AlbumId.of("a3la23bu91m"))
                .artists(artists)
                .totalTrackCount(2)
                .albumType(AlbumSpec.AlbumType.EPISODE)
                .name("Sarah")
                .images(images)
                .build();

        var item = TrackItem.builder()
                .id("04nJixim5a0MAz3PGiVID1")
                .contextUri(ContextUri.forTrack("04nJixim5a0MAz3PGiVID1"))
                .name("Something")
                .explicit(true)
                .duration(PlayableItemDuration.ofMilliseconds(790024))
                .artists(artists)
                .order(TrackItemSpec.Order.of(1, 1))
                .album(albumInfo)
                .build();

        ImageList images2 = ImageList.builder()
                .image(Image.builder().url(URI.create("https://i.pinimg.com/564x/90/bc/a8/90bca83aa94a664206a7e4c305888023.jpg")).build())
                .build();


        Album albumInfo2 = Album.builder()
                .id(AlbumSpec.AlbumId.of("miku123"))
                .artists(artists)
                .totalTrackCount(2)
                .albumType(AlbumSpec.AlbumType.EPISODE)
                .name("Sarah")
                .images(images2)
                .build();

        var item2 = TrackItem.builder()
                .id("miku123")
                .contextUri(ContextUri.forTrack("miku123"))
                .name("Something")
                .explicit(true)
                .duration(PlayableItemDuration.ofMilliseconds(790024))
                .artists(artists)
                .order(TrackItemSpec.Order.of(1, 1))
                .album(albumInfo2)
                .build();

        logger.info("Created new playable items for local dev only with ids: {}, {}", item.getId(), item2.getId());

        return new PredefinedPlayableItemLoader(List.of(
                item, item2
        ));
    }
}
