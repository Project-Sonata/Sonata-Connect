package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.model.PlayableItemDuration;
import org.apache.commons.lang3.RandomStringUtils;

public final class TrackItemEntityFaker {
    private final TrackItemEntity.TrackItemEntityBuilder builder = TrackItemEntity.builder();
    private final Faker faker = Faker.instance();

    public TrackItemEntityFaker() {
        builder.id(RandomStringUtils.randomAlphanumeric(22))
                .name(faker.rockBand().name())
                .duration(PlayableItemDuration.ofMilliseconds(
                        faker.random().nextLong(256_000L)
                ));
    }

    public static TrackItemEntityFaker create() {
        return new TrackItemEntityFaker();
    }

    public TrackItemEntity get() {
        return builder.build();
    }
}
