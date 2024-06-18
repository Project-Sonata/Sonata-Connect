package testing.faker;

import com.odeyalo.sonata.connect.entity.AlbumEntity;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;

public final class AlbumEntityFaker {
    private final AlbumEntity.AlbumEntityBuilder builder = AlbumEntity.builder();

    public AlbumEntityFaker() {
        final AlbumSpec.AlbumId albumId = AlbumSpec.AlbumId.random();

        builder
                .id(albumId);
    }

    public static AlbumEntityFaker create() {
        return new AlbumEntityFaker();
    }

    public AlbumEntity get() {
        return builder.build();
    }
}
