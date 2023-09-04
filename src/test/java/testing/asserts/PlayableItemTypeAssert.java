package testing.asserts;

import com.odeyalo.sonata.connect.model.PlayableItemType;
import org.assertj.core.api.AbstractAssert;

import static com.odeyalo.sonata.connect.model.PlayableItemType.PODCAST;
import static com.odeyalo.sonata.connect.model.PlayableItemType.TRACK;

/**
 * Asserts for PlayableItemType
 */
public class PlayableItemTypeAssert extends AbstractAssert<PlayableItemTypeAssert, PlayableItemType> {
    protected PlayableItemTypeAssert(PlayableItemType actual, Class<?> self) {
        super(actual, self);
    }

    public PlayableItemTypeAssert(PlayableItemType actual) {
        super(actual, PlayableItemTypeAssert.class);
    }

    public PlayableItemTypeAssert track() {
        return currentPlayingTypeAssert(TRACK);
    }

    public PlayableItemTypeAssert podcast() {
        return currentPlayingTypeAssert(PODCAST);
    }

    private PlayableItemTypeAssert currentPlayingTypeAssert(PlayableItemType expected) {
        if (actual !=  expected) {
            failWithActualExpectedAndMessage(actual, expected, "Expected the playable item type to be:");
        }
        return this;
    }
}
