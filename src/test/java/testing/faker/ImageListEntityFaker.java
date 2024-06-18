package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.ImageEntity;
import com.odeyalo.sonata.connect.entity.ImageListEntity;
import com.odeyalo.sonata.connect.model.track.Image;
import com.odeyalo.sonata.connect.model.track.ImageList;

public final class ImageListEntityFaker {
    private final ImageListEntity.ImageListEntityBuilder builder = ImageListEntity.builder();
    private final Faker faker = Faker.instance();

    public ImageListEntityFaker() {
        final int artistSize = faker.random().nextInt(0, 5);

        for (int i = 0; i < artistSize; i++) {
            final ImageEntity image = ImageEntityFaker.create().get();

            builder.image(image);

        }
    }

    public static ImageListEntityFaker create() {
        return new ImageListEntityFaker();
    }

    public ImageListEntity get() {
        return builder.build();
    }
}
