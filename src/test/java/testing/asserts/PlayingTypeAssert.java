package testing.asserts;

import com.odeyalo.sonata.connect.model.PlayingType;
import org.assertj.core.api.AbstractAssert;

public class PlayingTypeAssert extends AbstractAssert<PlayingTypeAssert, PlayingType> {

    public PlayingTypeAssert(PlayingType actual) {
        super(actual, PlayingTypeAssert.class);
    }

    protected PlayingTypeAssert(PlayingType playingType, Class<?> selfType) {
        super(playingType, selfType);
    }

}
