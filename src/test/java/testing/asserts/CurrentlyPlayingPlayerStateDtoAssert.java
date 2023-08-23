package testing.asserts;

import com.odeyalo.sonata.connect.dto.CurrentlyPlayingPlayerStateDto;
import org.assertj.core.api.AbstractAssert;

public class CurrentlyPlayingPlayerStateDtoAssert extends AbstractAssert<CurrentlyPlayingPlayerStateDtoAssert, CurrentlyPlayingPlayerStateDto> {

    public CurrentlyPlayingPlayerStateDtoAssert(CurrentlyPlayingPlayerStateDto actual) {
        super(actual, CurrentlyPlayingPlayerStateDtoAssert.class);
    }

    public static CurrentlyPlayingPlayerStateDtoAssert forBody(CurrentlyPlayingPlayerStateDto actual) {
        return new CurrentlyPlayingPlayerStateDtoAssert(actual);
    }


    public ShuffleStateAsserts shuffleState() {
        return new ShuffleStateAsserts(actual.getShuffleState());
    }
}
