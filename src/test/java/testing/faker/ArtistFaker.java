package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.Artist;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;

public final class ArtistFaker {
    private final Artist.ArtistBuilder builder = Artist.builder();
    private final Faker faker = Faker.instance();

    public ArtistFaker() {
        ArtistSpec.ArtistId artistId = ArtistSpec.ArtistId.random();

        builder
                .id(artistId)
                .name(faker.artist().name())
                .contextUri(artistId.toContextUri());
    }

    public static ArtistFaker create() {
        return new ArtistFaker();
    }

    public Artist get() {
        return builder.build();
    }
}
