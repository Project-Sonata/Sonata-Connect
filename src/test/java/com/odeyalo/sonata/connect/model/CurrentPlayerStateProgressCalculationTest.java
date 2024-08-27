package com.odeyalo.sonata.connect.model;

import org.junit.jupiter.api.Test;
import testing.faker.PlayableItemFaker;
import testing.time.TestingClock;

import java.time.Duration;
import java.time.Instant;

import static com.odeyalo.sonata.connect.model.DeviceSpec.DeviceStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

class CurrentPlayerStateProgressCalculationTest {
    static final PlayableItem SECONDS_30_PLAYABLE_ITEM = PlayableItemFaker.create()
            .setDuration(Duration.ofSeconds(30))
            .get();

    static final User USER = User.of("odeyalooo");

    static final Device DEVICE = Device.builder()
            .deviceId("miku")
            .deviceName("Odeyalo-PC")
            .deviceType(DeviceType.COMPUTER)
            .status(ACTIVE)
            .volume(Volume.fromInt(35))
            .build();

    @Test
    void shouldReturnDefaultValueIfNothingIsPlaying() {
        final CurrentPlayerState testable = CurrentPlayerState.emptyFor(USER)
                .withPlayableItem(null);

        assertThat(testable.getProgressMs()).isEqualTo(-1);
    }

    @Test
    void shouldNotReturnDefaultValueIfSomethingIsPlaying() {
        final CurrentPlayerState initialState = CurrentPlayerState.emptyFor(USER);

        final CurrentPlayerState withConnectedDevice = initialState.connectDevice(DEVICE);

        final CurrentPlayerState updatedState = withConnectedDevice.play(SECONDS_30_PLAYABLE_ITEM);

        assertThat(updatedState.getProgressMs()).isNotEqualTo(-1L);
    }

    @Test
    void shouldReturnTheSameProgressIfPlaybackIsPaused() {
        final CurrentPlayerState testable = CurrentPlayerState.emptyFor(USER)
                .withPlaying(false)
                .withProgressMs(2000L)
                .withPlayableItem(SECONDS_30_PLAYABLE_ITEM);

        assertThat(testable.getProgressMs()).isEqualTo(2000L);
        // check that the progress does not change every time when getProgressMs is called
        assertThat(testable.getProgressMs()).isEqualTo(2000L);
        assertThat(testable.getProgressMs()).isEqualTo(2000L);
    }

    @Test
    void shouldReturnCurrentProgressForItemThatPlayingNow() {

        final TestingClock clock = new TestingClock(Instant.now());

        final CurrentPlayerState initialState = CurrentPlayerState.emptyFor(USER)
                .useClock(clock);

        final CurrentPlayerState withConnectedDevice = initialState.connectDevice(DEVICE);

        final CurrentPlayerState testable = withConnectedDevice.play(SECONDS_30_PLAYABLE_ITEM);

        clock.waitSeconds(2);

        assertThat(testable.getProgressMs()).isEqualTo(2000L);

        clock.waitSeconds(4);

        assertThat(testable.getProgressMs()).isEqualTo(6000L);
    }

    @Test
    void shouldReturnProgressMsAfterThePauseCommand() {

        final TestingClock clock = new TestingClock(Instant.now());

        final CurrentPlayerState initialState = CurrentPlayerState.emptyFor(USER)
                .useClock(clock);

        final CurrentPlayerState withConnectedDevice = initialState.connectDevice(DEVICE);

        final CurrentPlayerState testable = withConnectedDevice.play(SECONDS_30_PLAYABLE_ITEM);

        clock.waitSeconds(6);

        assertThat(testable.getProgressMs()).isEqualTo(6000L);

        final CurrentPlayerState afterPause = testable.pause();

        clock.waitSeconds(10);

        assertThat(afterPause.getProgressMs()).isEqualTo(6000L);
    }

    @Test
    void shouldContinueProgressMsAfterThePointPlaybackWasPaused() {

        final TestingClock clock = new TestingClock(Instant.now());

        final CurrentPlayerState initialState = CurrentPlayerState.emptyFor(USER)
                .useClock(clock);

        final CurrentPlayerState withConnectedDevice = initialState.connectDevice(DEVICE);

        final CurrentPlayerState testable = withConnectedDevice.play(SECONDS_30_PLAYABLE_ITEM);

        clock.waitSeconds(6);

        final CurrentPlayerState afterPause = testable.pause();

        clock.waitSeconds(10);

        final CurrentPlayerState afterPlaybackResume = afterPause.resumePlayback();

        clock.waitSeconds(4);

        assertThat(afterPlaybackResume.getProgressMs()).isEqualTo(10_000L);
    }

    @Test
    void shouldProperlyCalculateTheProgressIfMillisPassed() {

        final TestingClock clock = new TestingClock(Instant.now());

        final CurrentPlayerState initialState = CurrentPlayerState.emptyFor(USER)
                .useClock(clock);

        final CurrentPlayerState withConnectedDevice = initialState.connectDevice(DEVICE);

        final CurrentPlayerState testable = withConnectedDevice.play(SECONDS_30_PLAYABLE_ITEM);

        clock.waitMillis(60);

        assertThat(testable.getProgressMs()).isEqualTo(60L);
    }

    @Test
    void shouldReturnEndOfTheProgressIfProgressIsOutOfBoundsOfPlayableItem() {
        final TestingClock clock = new TestingClock(Instant.now());

        final CurrentPlayerState initialState = CurrentPlayerState.emptyFor(USER)
                .useClock(clock);

        final PlayableItem playableItem = PlayableItemFaker.create()
                .setDuration(Duration.ofSeconds(230))
                .get();


        final CurrentPlayerState withConnectedDevice = initialState.connectDevice(DEVICE);

        final CurrentPlayerState testable = withConnectedDevice.play(playableItem);

        clock.waitSeconds(240);

        assertThat(testable.getProgressMs()).isEqualTo(230_000L);
    }

    @Test
    void shouldReturnEndOfTheProgressIfProgressIsEqualToItemDuration() {
        final TestingClock clock = new TestingClock(Instant.now());

        final CurrentPlayerState initialState = CurrentPlayerState.emptyFor(USER)
                .useClock(clock);

        final PlayableItem playableItem = PlayableItemFaker.create()
                .setDuration(Duration.ofSeconds(230))
                .get();

        final CurrentPlayerState withConnectedDevice = initialState.connectDevice(DEVICE);

        final CurrentPlayerState testable = withConnectedDevice.play(playableItem);

        clock.waitSeconds(230);

        final CurrentPlayerState pausedPlayback = testable.pause();

        assertThat(pausedPlayback.getProgressMs()).isEqualTo(230_000L);
    }
}
