package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.ArtistEntity;
import com.odeyalo.sonata.connect.entity.ArtistListEntity;

public final class ArtistListEntityFaker {
    private final ArtistListEntity.ArtistListEntityBuilder builder = ArtistListEntity.builder();
    private final Faker faker = Faker.instance();

    public ArtistListEntityFaker() {
        final int artistSize = faker.random().nextInt(0, 5);

        for (int i = 0; i < artistSize; i++) {
            final ArtistEntity artist = ArtistEntityFaker.create().get();

            builder.artist(artist);

        }
    }

    public static ArtistListEntityFaker create() {
        return new ArtistListEntityFaker();
    }

    public ArtistListEntity get() {
        return builder.build();
    }
}
