package com.odeyalo.sonata.connect.model.track;

import com.odeyalo.sonata.common.context.ContextUri;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Describe information that all artists should have.
 */
public interface ArtistSpec {

    @NotNull
    ArtistId getId();

    @NotNull
    String getName();

    @NotNull
    default ContextUri getContextUri() {
        return getId().toContextUri();
    }

    record ArtistId(@NotNull String value) {

        @NotNull
        public static ArtistId of(@NotNull final String id) {
            return new ArtistId(id);
        }

        /**
         * Generate a pseudo-random ID for the artist.
         * NOTE: there is a chance that generate will be the same as existing. This cases should be handled properly!
         * @return - pseudo-random ID for the artist
         */
        public static ArtistId random() {
            return ArtistId.of(
                    RandomStringUtils.randomAlphanumeric(22)
            );
        }

        @NotNull
        public ContextUri toContextUri() {
            return ContextUri.forArtist(value);
        }
    }
}
