package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.connect.exception.MissingPlayableItemException;
import com.odeyalo.sonata.connect.exception.SeekPositionExceedDurationException;
import com.odeyalo.sonata.connect.service.player.SeekPosition;
import org.junit.jupiter.api.Test;
import testing.faker.PlayableItemFaker;
import testing.time.TestingClock;

import java.time.Duration;
import java.time.Instant;

import static com.odeyalo.sonata.connect.model.DeviceSpec.DeviceStatus.ACTIVE;
import static org.assertj.core.api.Assertions.*;

public final class SeekToPositionTest {

    static final PlayableItem SIMPLE_TRACK = PlayableItemFaker.create()
            .setDuration(Duration.ofSeconds(200))
            .get();

    static final User USER = User.of("123");

    static final Device DEVICE = Device.builder()
            .deviceId("miku")
            .deviceName("Odeyalo-PC")
            .deviceType(DeviceType.COMPUTER)
            .status(ACTIVE)
            .volume(Volume.fromInt(35))
            .build();


    @Test
    void shouldProperlySeekPlayerProgressToPosition() {
        final TestingClock timer = new TestingClock(Instant.now());

        final CurrentPlayerState initialPlayer = CurrentPlayerState.emptyFor(USER)
                .useClock(timer);

        final CurrentPlayerState withConnectedDevices = initialPlayer.connectDevice(DEVICE);

        final CurrentPlayerState afterPlay = withConnectedDevices.play(SIMPLE_TRACK);

        timer.waitSeconds(5);

        final CurrentPlayerState afterSeek = afterPlay.seekTo(SeekPosition.ofMillis(1000));

        assertThat(afterSeek.getProgressMs()).isEqualTo(1000);
    }

    @Test
    void shouldThrowExceptionIfSeekPositionIsGreaterThanTrackDuration() {
        final CurrentPlayerState initialPlayer = CurrentPlayerState.emptyFor(USER);

        final CurrentPlayerState withConnectedDevices = initialPlayer.connectDevice(DEVICE);

        final CurrentPlayerState afterPlay = withConnectedDevices.play(SIMPLE_TRACK);


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
