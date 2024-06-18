package com.odeyalo.sonata.connect.service.player.support;

import com.odeyalo.sonata.common.context.ContextEntityType;
import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.model.track.Album;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
import jakarta.ws.rs.NotSupportedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.odeyalo.sonata.common.context.ContextEntityType.TRACK;
import static com.odeyalo.sonata.connect.model.track.AlbumSpec.AlbumType.SINGLE;

/**
 * Implementation of a PlayableItemResolver that is hardcoded and not designed to be easily scalable
 * It can be used in tests to reduce object creation.
 */
@Component
public class HardcodedPlayableItemLoader implements PlayableItemLoader {

    @Override
    @NotNull
    public Mono<PlayableItem> resolveItem(@NotNull final ContextUri contextUri) {
        ContextEntityType type = contextUri.getType();
        if (type != TRACK) {
            return Mono.error(new NotSupportedException("Only track is supported"));
        }
        return Mono.just(TrackItem.of(contextUri.getEntityId(),
                "mock",
                PlayableItemDuration.ofSeconds(100),
                ContextUri.forTrack("mock"),
                false,
                TrackItemSpec.Order.of(0, 1),
                ArtistList.solo(Artist.of(ArtistSpec.ArtistId.of("123"), "BONES", ContextUri.forArtist("123"))),
                Album.of(
                        AlbumSpec.AlbumId.of("123"),
                        "something",
                        SINGLE,
                        2
                ))
        );
    }
}
