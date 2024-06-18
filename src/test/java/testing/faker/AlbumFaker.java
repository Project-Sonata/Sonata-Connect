package testing.faker;

import com.odeyalo.sonata.connect.model.track.Album;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;

public final class AlbumFaker {
    private final Album.AlbumBuilder builder = Album.builder();

    public AlbumFaker() {
        final AlbumSpec.AlbumId albumId = AlbumSpec.AlbumId.random();

        builder
                .id(albumId);
    }

    public static AlbumFaker create() {
        return new AlbumFaker();
    }

    public Album get() {
        return builder.build();
    }
}
