package testing.asserts;

import com.odeyalo.sonata.connect.dto.TrackItemDto;

import static org.assertj.core.api.Assertions.assertThat;

public class TrackItemDtoAssert extends PlayingItemDtoAssert<TrackItemDtoAssert, TrackItemDto> {

    public TrackItemDtoAssert(TrackItemDto actual) {
        super(actual, TrackItemDtoAssert.class);
    }

    public TrackItemDtoAssert hasName(final String expected) {
        assertThat(actual.getName()).isEqualTo(expected);
        return this;
    }
}
