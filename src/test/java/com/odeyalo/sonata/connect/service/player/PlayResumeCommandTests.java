package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.exception.PlayableItemNotFoundException;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.TrackItem;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayableItemFaker.TrackItemFaker;
import testing.faker.TrackItemEntityFaker;

import static com.odeyalo.sonata.connect.model.PlayableItemType.TRACK;
import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.CURRENT_DEVICE;
import static org.assertj.core.api.Assertions.assertThat;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class PlayResumeCommandTests extends DefaultPlayerOperationsTest {
    static final ContextUri TRACK_1_CONTEXT_URI = ContextUri.forTrack("cassie");
    static final ContextUri TRACK_2_CONTEXT_URI = ContextUri.forTrack("miku13");

    static final TrackItem TRACK_1 = TrackItemFaker.create().withId("cassie").get();
    static final TrackItem TRACK_2 = TrackItemFaker.create().withId("miku13").get();

    @Test
    void shouldResumePlaybackIfContextUriIsEqualToProvided() {
        final TrackItemEntity playingItem = TrackItemEntityFaker.create()
                .withId("cassie")
                .get();

        final PlayerStateEntity playerState = existingPlayerState()
                .setPlaying(false)
                .setCurrentlyPlayingItem(playingItem)
                .setPlayingType(PlayingType.TRACK);

        final DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .withPlayableItems(TRACK_1)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.of(TRACK_1_CONTEXT_URI), CURRENT_DEVICE)
                .as(StepVerifier::create)
                .assertNext(state -> {
                    assertThat(state.isPlaying()).isTrue();
                    assertThat(state.getPlayableItem()).isNotNull();
                    assertThat(state.getPlayableItem().getId()).isEqualTo("cassie");
                    assertThat(state.getPlayableItem().getContextUri()).isEqualTo(TRACK_1_CONTEXT_URI);
                    assertThat(state.getPlayableItem().getItemType()).isEqualTo(TRACK);
                })
                .verifyComplete();
    }

    @Test
    void shouldResumePlaybackIfProvidedContextUriIsMissing() {
        final TrackItemEntity playingItem = TrackItemEntityFaker.create()
                .withId("cassie")
                .get();

        final PlayerStateEntity playerState = existingPlayerState()
                .setPlaying(false)
                .setCurrentlyPlayingItem(playingItem)
                .setPlayingType(PlayingType.TRACK);

        final DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .withPlayableItems(TRACK_1)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.resumePlayback(), CURRENT_DEVICE)
                .as(StepVerifier::create)
                .assertNext(state -> {
                    assertThat(state.isPlaying()).isTrue();
                    assertThat(state.getPlayableItem()).isNotNull();
                    assertThat(state.getPlayableItem().getId()).isEqualTo("cassie");
                    assertThat(state.getPlayableItem().getContextUri()).isEqualTo(TRACK_1_CONTEXT_URI);
                    assertThat(state.getPlayableItem().getItemType()).isEqualTo(TRACK);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorIfPlayableItemByContextUriDoesNotExist() {
        final PlayerStateEntity playerState = existingPlayerState();

        final DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.of(ContextUri.forTrack("not_exist") ), CURRENT_DEVICE)
                .as(StepVerifier::create)
                .expectError(PlayableItemNotFoundException.class)
                .verify();
    }

    @Test
    void shouldStartPlayingNewItemIfOtherItemWasProvided() {
        final TrackItemEntity playingItem = TrackItemEntityFaker.create()
                .withId("cassie")
                .get();

        final PlayerStateEntity playerState = existingPlayerState()
                .setPlaying(false)
                .setCurrentlyPlayingItem(playingItem)
                .setPlayingType(PlayingType.TRACK);

        final DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .withPlayableItems(TRACK_1, TRACK_2)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.of(TRACK_2_CONTEXT_URI), CURRENT_DEVICE)
                .as(StepVerifier::create)
                .assertNext(state -> {
                    assertThat(state.isPlaying()).isTrue();
                    assertThat(state.getPlayableItem()).isNotNull();
                    assertThat(state.getPlayableItem().getId()).isEqualTo("miku13");
                    assertThat(state.getPlayableItem().getContextUri()).isEqualTo(TRACK_2_CONTEXT_URI);
                    assertThat(state.getPlayableItem().getItemType()).isEqualTo(TRACK);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorIfNoDevicesAreConnected() {
        final PlayerStateEntity playerState = existingPlayerState()
                .setDevicesEntity(DevicesEntity.empty());

        final DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .withPlayableItems(TRACK_1)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.from(TRACK_1_CONTEXT_URI), CURRENT_DEVICE)
                .as(StepVerifier::create)
                .expectError(NoActiveDeviceException.class)
                .verify();
    }

}
