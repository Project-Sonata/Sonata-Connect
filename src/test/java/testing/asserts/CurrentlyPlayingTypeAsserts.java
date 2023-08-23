package testing.asserts;

import org.assertj.core.api.AbstractAssert;

public class CurrentlyPlayingTypeAsserts extends AbstractAssert<CurrentlyPlayingTypeAsserts, String> {
    public static final String TRACK = "track";
    public static final String PODCAST = "podcast";

    CurrentlyPlayingTypeAsserts(String actual) {
        super(actual, CurrentlyPlayingTypeAsserts.class);
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
}
