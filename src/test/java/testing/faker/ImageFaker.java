package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.connect.model.track.Image;

import java.net.URI;

public final class ImageFaker {
    private final Image.ImageBuilder builder = Image.builder();
    private final Faker faker = Faker.instance();

    public ImageFaker() {
        Integer size = faker.random().nextInt(50, 600);

        builder
                .url(URI.create(faker.internet().image()))
                .height(size)
                .width(size);
    }

    public static ImageFaker create() {
        return new ImageFaker();
    }

    public Image get() {
        return builder.build();
    }
}
