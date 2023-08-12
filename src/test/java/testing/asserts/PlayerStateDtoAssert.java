package testing.asserts;

import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.model.RepeatState;
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

    public PlayerStateDtoAssert shouldBeStopped() {
        if (actual.isPlaying()) {
            failWithActualExpectedAndMessage(true, false, "Expected player with paused status!");
        }
        return this;
    }

    public RepeatStateAssertWrapper repeatState() {
        return new RepeatStateAssertWrapper(actual.getRepeatState(), this);
    }

    public ShuffleStateAsserts shuffleState() {
        return new ShuffleStateAsserts(actual.getShuffleState(), this);
    }

    public CurrentlyPlayingTypeAsserts currentlyPlayingType() {
        return new CurrentlyPlayingTypeAsserts(actual.getCurrentlyPlayingType(), this);
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

    public static class ShuffleStateAsserts extends AbstractAssert<ShuffleStateAsserts, Boolean> implements ParentAssertAware {
        private final PlayerStateDtoAssert parent;

        public static final boolean ON = true;
        public static final boolean OFF = false;

        public ShuffleStateAsserts(Boolean actual, PlayerStateDtoAssert parent) {
            super(actual, ShuffleStateAsserts.class);
            this.parent = parent;
        }

        public ShuffleStateAsserts on() {
            return shuffleStateAssert(ON);
        }

        public ShuffleStateAsserts off() {
            return shuffleStateAssert(OFF);
        }

        private ShuffleStateAsserts shuffleStateAssert(boolean expected) {
            if (actual != expected) {
                failWithActualExpectedAndMessage(actual, expected, "The player state should be: %s(%s)", expected ? "ON" : "OFF", expected);
            }
            return this;
        }

        @Override
        public PlayerStateDtoAssert and() {
            return parent;
        }
    }

    public static class CurrentlyPlayingTypeAsserts extends AbstractAssert<CurrentlyPlayingTypeAsserts, String> implements ParentAssertAware {
        private final PlayerStateDtoAssert parent;
        public static final String TRACK = "track";
        public static final String PODCAST = "podcast";

        private CurrentlyPlayingTypeAsserts(String actual, PlayerStateDtoAssert parent) {
            super(actual, CurrentlyPlayingTypeAsserts.class);
            this.parent = parent;
        }

        public CurrentlyPlayingTypeAsserts track() {
            return currentPlayingTypeAssert(TRACK);
        }

        public CurrentlyPlayingTypeAsserts podcast() {
            return currentPlayingTypeAssert(PODCAST);
        }

        private CurrentlyPlayingTypeAsserts currentPlayingTypeAssert(String expected) {
            if (!actual.equals(expected)) {
                failWithActualExpectedAndMessage(actual, expected, "Expected the playing type to be:");
            }
            return this;
        }

        @Override
        public PlayerStateDtoAssert and() {
            return parent;
        }
    }
}
