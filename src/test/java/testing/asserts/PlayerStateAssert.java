package testing.asserts;

import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.RepeatState;
import org.assertj.core.api.AbstractAssert;
import org.springframework.util.Assert;

import static org.apache.commons.lang.BooleanUtils.isFalse;

public class PlayerStateAssert extends AbstractAssert<PlayerStateAssert, PlayerState> {

    private PlayerStateAssert(PlayerState actual) {
        super(actual, PlayerStateAssert.class);
    }

    public static PlayerStateAssert forState(PlayerState actual) {
        Assert.notNull(actual, "Actual value must be not null!");
        return new PlayerStateAssert(actual);
    }

    public PlayerStateAssert shouldPlay() {
        if ( isFalse(actual.isPlaying()) ) {
            failWithActualExpectedAndMessage(false, true, "Expected player with playing status!");
        }
        return this;
    }

    public PlayerStateAssert shouldBeStopped() {
        if ( actual.isPlaying() ) {
            failWithActualExpectedAndMessage(true, false, "Expected player with paused status!");
        }
        return this;
    }

    public RepeatStateAssertWrapper repeatState() {
        return new RepeatStateAssertWrapper(actual.getRepeatState(), this);
    }

    public ShuffleStateAssertsWrapper shuffleState() {
        return new ShuffleStateAssertsWrapper(actual.getShuffleState(), this);
    }

    public CurrentlyPlayingTypeAssertsWrapper currentlyPlayingType() {
        return new CurrentlyPlayingTypeAssertsWrapper(actual.getCurrentlyPlayingType().name(), this);
    }

    public PlayerStateAssert progressMs(long expectedMs) {
        if ( actual.getProgressMs() != expectedMs ) {
            failWithActualExpectedAndMessage(actual.getProgressMs(), expectedMs, "The progress in ms must be equalQ!");
        }
        return null;
    }

    public DevicesEntityAssertWrapper devices() {
        return new DevicesEntityAssertWrapper(actual.getDevices(), this);
    }

    public PlayableItemEntityAssert playingItem() {
        return new PlayableItemEntityAssert(actual.getCurrentlyPlayingItem());
    }

    interface ParentAssertAware {
        PlayerStateAssert and();
    }

    public static class RepeatStateAssertWrapper extends RepeatStateAssert implements ParentAssertAware {

        private final PlayerStateAssert parent;

        protected RepeatStateAssertWrapper(RepeatState actual, PlayerStateAssert parent) {
            super(actual);
            this.parent = parent;
        }

        @Override
        public PlayerStateAssert and() {
            return parent;
        }
    }

    public static class DevicesEntityAssertWrapper extends DevicesEntityAssert implements ParentAssertAware {
        private final PlayerStateAssert parent;

        protected DevicesEntityAssertWrapper(DevicesEntity actual, PlayerStateAssert parent) {
            super(actual);
            this.parent = parent;
        }

        @Override
        public PlayerStateAssert and() {
            return parent;
        }
    }

    public static class ShuffleStateAssertsWrapper extends ShuffleStateAsserts implements ParentAssertAware {
        private final PlayerStateAssert parent;

        public ShuffleStateAssertsWrapper(Boolean actual, PlayerStateAssert parent) {
            super(actual);
            this.parent = parent;
        }

        @Override
        public PlayerStateAssert and() {
            return parent;
        }
    }

    public static class CurrentlyPlayingTypeAssertsWrapper extends CurrentlyPlayingTypeAsserts implements ParentAssertAware {
        private final PlayerStateAssert parent;

        private CurrentlyPlayingTypeAssertsWrapper(String actual, PlayerStateAssert parent) {
            super(actual);
            this.parent = parent;
        }

        @Override
        public PlayerStateAssert and() {
            return parent;
        }
    }
}
