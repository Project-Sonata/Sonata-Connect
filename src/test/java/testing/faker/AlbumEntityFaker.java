package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.AlbumEntity;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;

public final class AlbumEntityFaker {
    private final AlbumEntity.AlbumEntityBuilder builder = AlbumEntity.builder();
    private final Faker faker = Faker.instance();

    public AlbumEntityFaker() {
        final AlbumSpec.AlbumId albumId = AlbumSpec.AlbumId.random();

        builder
                .id(albumId)
                .name(faker.music().chord())
                .albumType(faker.options().option(AlbumSpec.AlbumType.class))
                .totalTrackCount(faker.random().nextInt(1, 10))
                .artists(ArtistListEntityFaker.create().get())
                .images(ImageListEntityFaker.create().get());
    }

    public static AlbumEntityFaker create() {
        return new AlbumEntityFaker();
    }

    public AlbumEntity get() {
        return builder.build();
    }
}
