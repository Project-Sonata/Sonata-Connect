package com.odeyalo.sonata.connect.model.track;

import com.odeyalo.sonata.common.context.ContextUri;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

public interface AlbumSpec {

    @NotNull
    AlbumId getId();

    @NotNull
    String getName();

    @NotNull
    AlbumType getAlbumType();

    int getTotalTrackCount();

    @NotNull
    ArtistListSpec<? extends ArtistSpec> getArtists();

    @NotNull
    ImageListSpec<? extends ImageSpec> getImages();

    @NotNull
    default ContextUri getContextUri() {
        return getId().toContextUrI();
    }

    record AlbumId(@NotNull String value) {

        public static AlbumId of(@NotNull final String value) {
            return new AlbumId(value);
        }

        /**
         * Generate pseudo-random {@link AlbumId}
         * NOTE: this factory method MAY produce existing album ids, those cases should be handled
         * @return - generated ID
         */
        public static AlbumId random() {
            return of(
                    RandomStringUtils.randomAlphanumeric(22)
            );
        }

        public ContextUri toContextUrI() {
            return ContextUri.forAlbum(value);
        }
    }

    enum AlbumType {
        SINGLE,
        EPISODE,
        ALBUM
    }
}
