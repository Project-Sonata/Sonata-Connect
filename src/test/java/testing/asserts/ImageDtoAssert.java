package testing.asserts;

import com.odeyalo.sonata.connect.dto.ImageDto;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public final class ImageDtoAssert extends AbstractAssert<ImageDtoAssert, ImageDto> {

    ImageDtoAssert(final ImageDto imageDto) {
        super(imageDto, ImageDtoAssert.class);
    }

    public static ImageDtoAssert forImageDto(final ImageDto value) {
        return new ImageDtoAssert(value);
    }

    public ImageDtoAssert hasUri(final String uri) {
        assertThat(actual.getUrl()).isEqualTo(uri);

        return this;
    }

    public ImageDtoAssert hasWidth(final int width) {
        assertThat(actual.getWidth()).isEqualTo(width);

        return this;
    }

    public ImageDtoAssert hasHeight(final int height) {
        assertThat(actual.getHeight()).isEqualTo(height);

        return this;
    }
}
