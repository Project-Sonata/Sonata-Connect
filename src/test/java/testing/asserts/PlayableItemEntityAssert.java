package testing.asserts;

import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import org.assertj.core.api.AbstractAssert;

public class PlayableItemEntityAssert extends AbstractAssert<PlayableItemEntityAssert, PlayableItemEntity> {

    public PlayableItemEntityAssert(PlayableItemEntity actual) {
        super(actual, PlayableItemEntityAssert.class);
    }

    protected PlayableItemEntityAssert(PlayableItemEntity actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static PlayableItemEntityAssert forEntity(PlayableItemEntity actual) {
        return new PlayableItemEntityAssert(actual);
    }

    public IdAssert id() {
        return new IdAssert(actual.getId());
    }

    public PlayableItemTypeAssert entityType() {
        return new PlayableItemTypeAssert(actual.getType());
    }
}
