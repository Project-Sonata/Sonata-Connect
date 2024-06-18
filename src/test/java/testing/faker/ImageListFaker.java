package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.Artist;
import com.odeyalo.sonata.connect.model.ArtistList;
import com.odeyalo.sonata.connect.model.track.Image;
import com.odeyalo.sonata.connect.model.track.ImageList;

public final class ImageListFaker {
    private final ImageList.ImageListBuilder builder = ImageList.builder();
    private final Faker faker = Faker.instance();

    public ImageListFaker() {
        final int artistSize = faker.random().nextInt(0, 5);

        for (int i = 0; i < artistSize; i++) {
            final Image image = ImageFaker.create().get();

            builder.image(image);

        }
    }

    public static ImageListFaker create() {
        return new ImageListFaker();
    }

    public ImageList get() {
        return builder.build();
    }
}
