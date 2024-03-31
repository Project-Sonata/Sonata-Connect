package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.exception.ReasonCodeAware;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayableItemType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.test.StepVerifier;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.CURRENT_DEVICE;
import static com.odeyalo.sonata.connect.service.player.DefaultPlayerOperationsTest.DefaultPlayerOperationsTestableBuilder.testableBuilder;

@Nested
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayResumeCommandTests extends DefaultPlayerOperationsTest {
    static final String INVALID_CONTEXT_URI = "sonata:invalid:cassie";
    static final String EXISTING_PLAYABLE_ITEM_CONTEXT = "sonata:track:cassie";

    @Test
    void shouldUpdatePlayerStateWithCorrectPlayableItemId() {
        PlayerState playerState = existingPlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playerState).build();

        testable.playOrResume(DefaultPlayerOperationsTest.EXISTING_USER, PlayCommandContext.of(EXISTING_PLAYABLE_ITEM_CONTEXT), CURRENT_DEVICE)
                .map(CurrentPlayerState::getPlayableItem)
                .map(PlayableItem::getId)
                .as(StepVerifier::create)
                .expectNext("cassie")
                .verifyComplete();
    }

    @Test
    void shouldUpdatePlayerStateWithCorrectPlayableItemType() {
        PlayerState playerState = existingPlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playerState).build();

        testable.playOrResume(DefaultPlayerOperationsTest.EXISTING_USER, PlayCommandContext.of(EXISTING_PLAYABLE_ITEM_CONTEXT), CURRENT_DEVICE)
                .map(CurrentPlayerState::getPlayableItem)
                .map(PlayableItem::getItemType)
                .as(StepVerifier::create)
                .expectNext(PlayableItemType.TRACK)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionIfContextUriIsInvalid() {
        PlayerState playerState = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.of(INVALID_CONTEXT_URI), CURRENT_DEVICE)
                .as(StepVerifier::create)
                .expectError(ReasonCodeAwareMalformedContextUriException.class)
                .verify();
    }

    @Test
    void shouldContainReasonCodeIfContextUriIsInvalid() {
        PlayerState playerState = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .build();

        testable.playOrResume(EXISTING_USER, PlayCommandContext.of(INVALID_CONTEXT_URI), CURRENT_DEVICE)
                .as(StepVerifier::create)
                .expectErrorMatches(err -> verifyReasonCode(err, "malformed_context_uri"))
                .verify();
    }

    private static boolean verifyReasonCode(Throwable err, String expected) {
        if ( err instanceof ReasonCodeAware reasonCodeAware ) {
            return reasonCodeAware.getReasonCode().equals(expected);
        }
        return false;
    }
}
