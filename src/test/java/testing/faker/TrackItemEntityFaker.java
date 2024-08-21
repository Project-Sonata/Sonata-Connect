package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.model.PlayableItemDuration;
import com.odeyalo.sonata.connect.model.TrackItemSpec;
import org.apache.commons.lang3.RandomStringUtils;

public final class TrackItemEntityFaker {
    private final TrackItemEntity.TrackItemEntityBuilder builder = TrackItemEntity.builder();
    private final Faker faker = Faker.instance();

    public TrackItemEntityFaker() {
        String trackId = RandomStringUtils.randomAlphanumeric(22);
        builder.id(trackId)
                .name(faker.rockBand().name())
                .duration(PlayableItemDuration.ofMilliseconds(
                        faker.random().nextLong(256_000L)
                ))
                .contextUri(ContextUri.forTrack(trackId))
                .explicit(faker.random().nextBoolean())
                .order(TrackItemSpec.Order.of(
                                faker.random().nextInt(0, 2),
                                faker.random().nextInt(0, 10)
                        ))
                .artists(
                        ArtistListEntityFaker.create().get()
                )
                .album(AlbumEntityFaker.create().get());
    }

    public static TrackItemEntityFaker create() {
        return new TrackItemEntityFaker();
    }



    public TrackItemEntity get() {
        return builder.build();
    }

    public TrackItemEntityFaker withId(final String id) {
        builder.id(id)
                .contextUri(ContextUri.forTrack(id));
        return this;
    }

    public TrackItemEntityFaker withDuration(final PlayableItemDuration duration) {
        builder.duration(duration);
        return this;
    }
}
