package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.*;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static com.odeyalo.sonata.connect.model.PlayableItemDuration.ofMilliseconds;

@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayableItemFaker {
    protected String id;
    protected PlayableItemDuration duration;

    public PlayableItemFaker() {
        this.id = RandomStringUtils.randomAlphanumeric(16);
    }

    public static PlayableItemFaker create() {
        return new PlayableItemFaker();
    }

    public PlayableItem get() {
        final TrackItemFaker builder = TrackItemFaker.create();

        if (duration != null) {
            builder.withDuration(duration);
        }

        return builder.get();
    }

    public PlayableItemFaker setDuration(final Duration itemDuration) {
        this.duration = PlayableItemDuration.fromJavaDuration(itemDuration);
        return this;
    }

    public static class TrackItemFaker extends PlayableItemFaker {
        private final TrackItem.TrackItemBuilder builder = TrackItem.builder();
        private final Faker faker = Faker.instance();

        public TrackItemFaker() {
            super();
            builder
                    .name(faker.internet().domainWord())
                    .contextUri(ContextUri.forTrack(id))
                    .explicit(faker.random().nextBoolean())
                    .order(TrackItemSpec.Order.of(
                            faker.random().nextInt(0, 2),
                            faker.random().nextInt(0, 10)
                    ))
                    .artists(
                            ArtistListFaker.create().get()
                    )
                    .album(AlbumFaker.create().get());
        }

        @NotNull
        public static TrackItemFaker create() {
            return new TrackItemFaker();
        }

        @NotNull
        public TrackItemFaker withContextUri(final String contextUri) {
            ContextUri uri = ContextUri.fromString(contextUri);
            this.id = uri.getEntityId();
            builder.contextUri(uri);
            return this;
        }

        public TrackItemFaker withId(final String id) {
            this.id = id;
            builder.contextUri(ContextUri.forTrack(id));
            return this;
        }

        public TrackItemFaker withDuration(final PlayableItemDuration duration) {
            this.duration = duration;
            builder.duration(duration);
            return this;
        }

        @Override
        public TrackItem get() {
            return builder
                    .id(id)
                    .duration(duration == null ? ofMilliseconds(faker.random().nextLong(256_000L)) : duration)
                    .build();
        }
    }
}
