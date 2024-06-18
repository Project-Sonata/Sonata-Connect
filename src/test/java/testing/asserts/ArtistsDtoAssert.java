package testing.asserts;

import com.odeyalo.sonata.connect.dto.ArtistDto;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.util.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ArtistsDtoAssert extends AbstractListAssert<ArtistsDtoAssert, List<? extends ArtistDto>, ArtistDto, ArtistDtoAssert> {

    ArtistsDtoAssert(final List<? extends ArtistDto> actual) {
        super(actual, ArtistsDtoAssert.class);
    }

    public static ArtistsDtoAssert assertThat(@NotNull final List<? extends ArtistDto> actual) {
        return new ArtistsDtoAssert(actual);
    }

    @Override
    protected ArtistDtoAssert toAssert(final ArtistDto value, final String description) {
        return new ArtistDtoAssert(value);
    }

    @Override
    protected ArtistsDtoAssert newAbstractIterableAssert(final Iterable<? extends ArtistDto> iterable) {
        return ArtistsDtoAssert.assertThat(
                Lists.newArrayList(iterable)
        );
    }

    public ArtistDtoAssert peekFirst() {
        if (actual.size() < 1) {
            throw new IllegalStateException("Missing Artist. Required at least one element");
        }
        return new ArtistDtoAssert(actual.get(0));
    }
}
