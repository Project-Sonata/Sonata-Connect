package testing.asserts;

import com.odeyalo.sonata.connect.dto.TrackItemDto;

public class TrackItemDtoAssert extends PlayingItemDtoAssert<TrackItemDtoAssert, TrackItemDto> {

    public TrackItemDtoAssert(TrackItemDto actual) {
        super(actual, TrackItemDtoAssert.class);
    }
}
