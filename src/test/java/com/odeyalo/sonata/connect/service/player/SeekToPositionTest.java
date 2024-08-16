package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.exception.MissingPlayableItemException;
import com.odeyalo.sonata.connect.exception.SeekPositionExceedDurationException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import testing.faker.PlayableItemFaker;
import testing.time.TestingClock;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

public final class SeekToPositionTest {

    public static final PlayableItem SIMPLE_TRACK = PlayableItemFaker.create()
            .setDuration(Duration.ofSeconds(200))
            .get();
    public static final User USER = User.of("123");

    @Test
    void shouldProperlySeekPlayerProgressToPosition() {
        final TestingClock timer = new TestingClock(Instant.now());

        final CurrentPlayerState initialPlayer = CurrentPlayerState.emptyFor(User.of("123"))
                .useClock(timer);

        final CurrentPlayerState afterPlay = initialPlayer.play(SIMPLE_TRACK);

        timer.waitSeconds(5);

        final CurrentPlayerState afterSeek = afterPlay.seekTo(SeekPosition.ofMillis(1000));

        assertThat(afterSeek.getProgressMs()).isEqualTo(1000);
    }

    @Test
    void shouldThrowExceptionIfSeekPositionIsGreaterThanTrackDuration() {
        final CurrentPlayerState initialPlayer = CurrentPlayerState.emptyFor(USER);

        final CurrentPlayerState afterPlay = initialPlayer.play(SIMPLE_TRACK);

        assertThatThrownBy(() -> afterPlay.seekTo(SeekPosition.ofMillis(Integer.MAX_VALUE)))
                .isInstanceOf(SeekPositionExceedDurationException.class);
    }

    @Test
    void shouldThrowExceptionIfThereIsNoPlayableItem() {
        final CurrentPlayerState initialPlayer = CurrentPlayerState.emptyFor(USER);

        assertThatThrownBy(() -> initialPlayer.seekTo(SeekPosition.ofMillis(1000)))
                .isInstanceOf(MissingPlayableItemException.class);
    }
}
