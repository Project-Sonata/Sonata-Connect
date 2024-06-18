package testing.asserts;

import com.odeyalo.sonata.connect.dto.ImageDto;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.util.Lists;

import java.util.List;

public final class ImageDtoListAssert extends AbstractListAssert<ImageDtoListAssert, List<? extends ImageDto>, ImageDto, ImageDtoAssert> {

    ImageDtoListAssert(final List<? extends ImageDto> imageDtoList) {
        super(imageDtoList, ImageDtoListAssert.class);
    }

    @Override
    protected ImageDtoAssert toAssert(final ImageDto value, final String description) {
        return ImageDtoAssert.forImageDto(value);
    }

    @Override
    protected ImageDtoListAssert newAbstractIterableAssert(final Iterable<? extends ImageDto> iterable) {
        return new ImageDtoListAssert(
                Lists.newArrayList(iterable)
        );
    }
}
