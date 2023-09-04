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

    public CurrentlyPlayingPlayerStateDtoAssert shouldPlay() {
        if (!actual.isPlaying()) {
            failWithMessage("Expected playing to be 'true'!");
        }
        return this;
    }

    public PlayingTypeAssert currentlyPlayingType() {
        return new PlayingTypeAssert(actual.getCurrentlyPlayingType());
    }

    public RepeatStateAssert repeatState() {
        return new RepeatStateAssert(actual.getRepeatState());
    }

    public PlayableItemDtoAssert currentlyPlayingItem() {
        return new PlayableItemDtoAssert(actual.getCurrentlyPlayingItem());
    }

    public DevicesDtoAssert devices() {
        return new DevicesDtoAssert(actual.getDevices());
    }
}
