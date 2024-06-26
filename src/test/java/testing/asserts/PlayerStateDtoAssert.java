package testing.asserts;

import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.dto.TrackItemDto;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.ShuffleMode;
import org.assertj.core.api.AbstractAssert;
import org.springframework.util.Assert;

import static org.apache.commons.lang.BooleanUtils.isFalse;

public class PlayerStateDtoAssert extends AbstractAssert<PlayerStateDtoAssert, PlayerStateDto> {

    private PlayerStateDtoAssert(PlayerStateDto actual) {
        super(actual, PlayerStateDtoAssert.class);
    }

    public static PlayerStateDtoAssert forState(PlayerStateDto actual) {
        Assert.notNull(actual, "Actual value must be not null!");
        return new PlayerStateDtoAssert(actual);
    }

    public PlayerStateDtoAssert shouldPlay() {
        if (isFalse(actual.isPlaying())) {
            failWithActualExpectedAndMessage(false, true, "Expected player with playing status!");
        }
        return this;
    }

    public PlayerStateDtoAssert shouldBePaused() {
        if (actual.isPlaying()) {
            failWithActualExpectedAndMessage(true, false, "Expected player with paused status!");
        }
        return this;
    }

    public RepeatStateAssertWrapper repeatState() {
        return new RepeatStateAssertWrapper(actual.getRepeatState(), this);
    }

    public ShuffleStateAssertsWrapper shuffleState() {
        return new ShuffleStateAssertsWrapper(actual.getShuffleState() ? ShuffleMode.ENABLED : ShuffleMode.OFF, this);
    }

    public CurrentlyPlayingTypeAssertsWrapper currentlyPlayingType() {
        return new CurrentlyPlayingTypeAssertsWrapper(actual.getCurrentlyPlayingType(), this);
    }

    public PlayerStateDtoAssert progressMs(long expectedMs) {
        if (actual.getProgressMs() != expectedMs) {
            failWithActualExpectedAndMessage(actual.getProgressMs(), expectedMs, "The progress in ms must be equalQ!");
        }
        return null;
    }

    public DevicesDtoAssertWrapper devices() {
        return new DevicesDtoAssertWrapper(actual.getDevices(), this);
    }

    public TrackItemDtoAssert track() {
        return new TrackItemDtoAssert((TrackItemDto) actual.getPlayingItem());
    }

    public ArtistsDtoAssert artists() {
        return new ArtistsDtoAssert(((TrackItemDto) actual.getPlayingItem()).getArtists());
    }

    public AlbumDtoAssert album() {
        return new AlbumDtoAssert(((TrackItemDto) actual.getPlayingItem()).getAlbum());
    }

    interface ParentAssertAware {
        PlayerStateDtoAssert and();
    }

    public static class RepeatStateAssertWrapper extends RepeatStateAssert implements ParentAssertAware {

        private final PlayerStateDtoAssert parent;

        protected RepeatStateAssertWrapper(RepeatState actual, PlayerStateDtoAssert parent) {
            super(actual);
            this.parent = parent;
        }

        @Override
        public PlayerStateDtoAssert and() {
            return parent;
        }
    }

    public static class DevicesDtoAssertWrapper extends DevicesDtoAssert implements ParentAssertAware {
        private final PlayerStateDtoAssert parent;

        protected DevicesDtoAssertWrapper(DevicesDto actual, PlayerStateDtoAssert parent) {
            super(actual);
            this.parent = parent;
        }

        @Override
        public PlayerStateDtoAssert and() {
            return parent;
        }
    }

    public static class ShuffleStateAssertsWrapper extends ShuffleStateAsserts implements ParentAssertAware {
        private final PlayerStateDtoAssert parent;

        public ShuffleStateAssertsWrapper(ShuffleMode actual, PlayerStateDtoAssert parent) {
            super(actual);
            this.parent = parent;
        }

        @Override
        public PlayerStateDtoAssert and() {
            return parent;
        }
    }

    public static class CurrentlyPlayingTypeAssertsWrapper extends CurrentlyPlayingTypeAsserts implements ParentAssertAware {
        private final PlayerStateDtoAssert parent;

        private CurrentlyPlayingTypeAssertsWrapper(String actual, PlayerStateDtoAssert parent) {
            super(actual);
            this.parent = parent;
        }

        @Override
        public PlayerStateDtoAssert and() {
            return parent;
        }
    }
}
