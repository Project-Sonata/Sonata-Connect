package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.TrackItem;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;

import static com.odeyalo.sonata.connect.model.PlayableItemDuration.ofMilliseconds;

@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayableItemFaker {
    protected String id;

    public PlayableItemFaker() {
        this.id = RandomStringUtils.randomAlphanumeric(16);
    }

    public static PlayableItemFaker create() {
        return new PlayableItemFaker();
    }

    public PlayableItem get() {
        return TrackItemFaker.create().get();
    }


    public static class TrackItemFaker extends PlayableItemFaker {
        private final TrackItem.TrackItemBuilder builder = TrackItem.builder();
        private final Faker faker = Faker.instance();

        public TrackItemFaker() {
            super();
            builder.name(faker.internet().domainWord())
                    .duration(ofMilliseconds(faker.random().nextLong(256_000L)));
        }

        public static TrackItemFaker create() {
            return new TrackItemFaker();
        }

        @Override
        public TrackItem get() {
            return builder.id(id)
                    .build();
        }
    }
}
