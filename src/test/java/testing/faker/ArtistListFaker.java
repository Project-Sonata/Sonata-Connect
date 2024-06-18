package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.Artist;
import com.odeyalo.sonata.connect.model.ArtistList;

public final class ArtistListFaker {
    private final ArtistList.ArtistListBuilder builder = ArtistList.builder();
    private final Faker faker = Faker.instance();

    public ArtistListFaker() {
        final int artistSize = faker.random().nextInt(0, 5);

        for (int i = 0; i < artistSize; i++) {
            final Artist artist = ArtistFaker.create().get();

            builder.artist(artist);

        }
    }

    public static ArtistListFaker create() {
        return new ArtistListFaker();
    }

    public ArtistList get() {
        return builder.build();
    }
}
