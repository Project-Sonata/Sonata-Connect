package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.exception.MissingPlayableItemException;
import com.odeyalo.sonata.connect.exception.SeekPositionExceedDurationException;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayableItemDuration;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.DevicesEntityFaker;
import testing.faker.PlayableItemFaker;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.CURRENT_DEVICE;
import static org.assertj.core.api.Assertions.assertThat;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class SeekToPositionOperationTest extends DefaultPlayerOperationsTest {

    public static final PlayableItem TRACK = PlayableItemFaker.TrackItemFaker.create().withContextUri("sonata:track:miku")
            .withDuration(PlayableItemDuration.ofSeconds(180))
            .get();

    @Test
    void shouldSeekToPosition() {
        PlayerStateEntity playerState = existingPlayerState()
                .setDevicesEntity(DevicesEntityFaker.create().get());

        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .withPlayableItems(TRACK)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.from(ContextUri.forTrack("miku")), CURRENT_DEVICE).block();

        testable.seekToPosition(EXISTING_USER, SeekPosition.ofSeconds(20))
                .as(StepVerifier::create)
                // don't want to state.useClock(MockClock) to not break the encapsulation so using isGreaterThanOrEqualTo
                .assertNext(state -> assertThat(state.getProgressMs()).isGreaterThanOrEqualTo(20_000))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorIfPositionExceedTheItemDuration() {
        PlayerStateEntity playerState = existingPlayerState()
                .setDevicesEntity(DevicesEntityFaker.create().get());

        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .withPlayableItems(TRACK)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.from(ContextUri.forTrack("miku")), CURRENT_DEVICE).block();

        testable.seekToPosition(EXISTING_USER, SeekPosition.ofSeconds(10_000))
                .as(StepVerifier::create)
                .expectError(SeekPositionExceedDurationException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorIfNothingIsPlaying() {
        PlayerStateEntity playerState = existingPlayerState()
                .setDevicesEntity(DevicesEntityFaker.create().get())
                .setCurrentlyPlayingItem(null)
                .setPlayingType(null);

        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .withPlayableItems(TRACK)
                .build();

        testable.seekToPosition(EXISTING_USER, SeekPosition.ofSeconds(20))
                .as(StepVerifier::create)
                .expectError(MissingPlayableItemException.class)
                .verify();
    }
}
