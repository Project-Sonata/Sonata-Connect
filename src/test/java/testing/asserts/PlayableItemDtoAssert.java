package testing.asserts;

import com.odeyalo.sonata.connect.dto.PlayableItemDto;
import org.assertj.core.api.AbstractAssert;

/**
 * Asserts for PlayableItemDto
 */
public class PlayableItemDtoAssert extends AbstractAssert<PlayableItemDtoAssert, PlayableItemDto> {

    protected PlayableItemDtoAssert(PlayableItemDto actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public PlayableItemDtoAssert(PlayableItemDto actual) {
        super(actual, PlayableItemDtoAssert.class);
    }

    public IdAssert id() {
        return new IdAssert(actual.getId());
    }

    public PlayableItemTypeAssert itemType() {
        return new PlayableItemTypeAssert(actual.getPlayingType());
    }

    public ContextUriAssert contextUri() {
        return new ContextUriAssert(null); // always null, not supported yet
    }
}
