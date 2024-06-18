package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.track.Album;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;

public final class AlbumFaker {
    private final Album.AlbumBuilder builder = Album.builder();
    private final Faker faker = Faker.instance();

    public AlbumFaker() {
        final AlbumSpec.AlbumId albumId = AlbumSpec.AlbumId.random();

        builder
                .id(albumId)
                .name(faker.music().chord())
                .albumType(faker.options().option(AlbumSpec.AlbumType.class))
                .totalTrackCount(faker.random().nextInt(1, 10))
                .artists(ArtistListFaker.create().get())
                .images(ImageListFaker.create().get());
    }

    public static AlbumFaker create() {
        return new AlbumFaker();
    }

    public Album get() {
        return builder.build();
    }
}
