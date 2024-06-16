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

    public TrackItemDtoAssert hasDurationMs(final long expectedDurationMs) {
        assertThat(actual.getDurationMs()).isEqualTo(expectedDurationMs);
        return this;
    }

    public TrackItemDtoAssert hasContextUri(final String expectedContextUri) {
        assertThat(actual.getContextUri()).isEqualTo(expectedContextUri);
        return this;
    }

    public TrackItemDtoAssert isExplicit(final boolean expected) {
        assertThat(actual.isExplicit()).isEqualTo(expected);
        return this;
    }

    public TrackItemDtoAssert hasDiscNumber(final int expectedDiscNumber) {
        assertThat(actual.getDiscNumber()).isEqualTo(expectedDiscNumber);
        return this;
    }
}
