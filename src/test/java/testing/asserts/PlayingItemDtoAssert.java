package testing.asserts;

import com.odeyalo.sonata.connect.dto.PlayableItemDto;
import org.assertj.core.api.AbstractAssert;

public class PlayingItemDtoAssert<SELF extends PlayingItemDtoAssert<SELF, ACTUAL>, ACTUAL extends PlayableItemDto> extends AbstractAssert<SELF, ACTUAL> {

    protected PlayingItemDtoAssert(ACTUAL actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public IdAssert id() {
        return new IdAssert(actual.getId());
    }

}
