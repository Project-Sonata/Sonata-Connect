package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.ArtistEntity;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;

public final class ArtistEntityFaker {
    private final ArtistEntity.ArtistEntityBuilder builder = ArtistEntity.builder();
    private final Faker faker = Faker.instance();

    public ArtistEntityFaker() {
        ArtistSpec.ArtistId artistId = ArtistSpec.ArtistId.random();

        builder
                .id(artistId)
                .name(faker.artist().name())
                .contextUri(artistId.toContextUri());
    }

    public static ArtistEntityFaker create() {
        return new ArtistEntityFaker();
    }

    public ArtistEntity get() {
        return builder.build();
    }
}
