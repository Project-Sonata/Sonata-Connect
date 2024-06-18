package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.track.ArtistListSpec;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.Comparator;

/**
 * Represent a Track item that can be played
 */
public interface TrackItemSpec extends PlayableItem {

    @NotNull
    String getName();

    @NotNull
    PlayableItemDuration getDuration();

    @NotNull
    ContextUri getContextUri();

    boolean isExplicit();

    @NotNull
    Order getOrder();

    @NotNull
    ArtistListSpec<? extends ArtistSpec> getArtists();

    @Override
    @NotNull
    default PlayableItemType getItemType() {
        return PlayableItemType.TRACK;
    }

    /**
     * Represent an Order of this track.
     * <p>
     * Order includes disc number and its index.
     *
     * <p>
     * {@link Order} implements a {@link Comparator} and by default sort the elements from
     * first to last, considering disc number and index of the tracK!
     *
     * @param discNumber - number of the disc on which this track appears
     * @param index - index of the track
     */
    record Order(int discNumber, int index) implements Comparable<Order> {

        public Order {
            Assert.state(index >= 0, "Index cannot be negative");
            Assert.state(discNumber >= 0, "Disc number cannot be negative");
        }

        public static Order of(int discNumber, int index) {
            return new Order(discNumber, index);
        }

        @Override
        public int compareTo(@NotNull final TrackItemSpec.Order o) {
            return Comparator.comparing(Order::discNumber)
                    .thenComparing(Order::index)
                    .compare(this, o);
        }
    }
}
