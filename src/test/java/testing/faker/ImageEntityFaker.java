package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.entity.ImageEntity;
import com.odeyalo.sonata.connect.model.track.Image;

import java.net.URI;

public final class ImageEntityFaker {
    private final ImageEntity.ImageEntityBuilder builder = ImageEntity.builder();
    private final Faker faker = Faker.instance();

    public ImageEntityFaker() {
        Integer size = faker.random().nextInt(50, 600);

        builder
                .url(URI.create(faker.internet().image()))
                .height(size)
                .width(size);;
    }

    public static ImageEntityFaker create() {
        return new ImageEntityFaker();
    }

    public ImageEntity get() {
        return builder.build();
    }
}
