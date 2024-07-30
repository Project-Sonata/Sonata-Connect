package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.TrackItem;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayableItemFaker.TrackItemFaker;

import static com.odeyalo.sonata.connect.model.PlayableItemType.TRACK;
import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.CURRENT_DEVICE;
import static org.assertj.core.api.Assertions.assertThat;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class PlayResumeCommandTests extends DefaultPlayerOperationsTest {
    static final ContextUri EXISTING_PLAYABLE_ITEM_CONTEXT = ContextUri.forTrack("cassie");
    static final TrackItem TRACK_1 = TrackItemFaker.create().withId("cassie").get();

    @Test
    void shouldUpdatePlayerStateWithCorrectPlayableItemId() {
        final PlayerStateEntity playerState = existingPlayerState();

        final DefaultPlayerOperations testable = testableBuilder().withState(playerState)
                .withPlayableItems(TRACK_1)
                .build();

        testable.playOrResume(DefaultPlayerOperationsTest.EXISTING_USER, PlayCommandContext.of(EXISTING_PLAYABLE_ITEM_CONTEXT), CURRENT_DEVICE)
                .mapNotNull(CurrentPlayerState::getPlayableItem)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getId()).isEqualTo("cassie"))
                .verifyComplete();
    }

    @Test
    void shouldUpdatePlayerStateWithCorrectPlayableItemType() {
        final PlayerStateEntity playerState = existingPlayerState();

        final DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .withPlayableItems(TRACK_1)
                .build();

        testable.playOrResume(DefaultPlayerOperationsTest.EXISTING_USER, PlayCommandContext.of(EXISTING_PLAYABLE_ITEM_CONTEXT), CURRENT_DEVICE)
                .mapNotNull(CurrentPlayerState::getPlayableItem)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getItemType()).isEqualTo(TRACK))
                .verifyComplete();
    }
}
