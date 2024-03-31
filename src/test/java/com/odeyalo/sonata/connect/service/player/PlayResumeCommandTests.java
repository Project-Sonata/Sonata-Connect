package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.exception.ReasonCodeAware;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItemType;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.CURRENT_DEVICE;
import static com.odeyalo.sonata.connect.service.player.DefaultPlayerOperationsTest.DefaultPlayerOperationsTestableBuilder.testableBuilder;
import static org.assertj.core.api.Assertions.assertThat;

@Nested
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayResumeCommandTests extends DefaultPlayerOperationsTest {
    public static final String INVALID_CONTEXT_URI = "sonata:invalid:cassie";
    PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

    public static final String EXISTING_PLAYABLE_ITEM_CONTEXT = "sonata:track:cassie";

    @AfterEach
    void cleanup() {
        playerStateRepository.clear().block();
    }

    @Test
    void shouldUpdateState() {
        PlayerState playerState = existingPlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playerState).build();

        testable.playOrResume(DefaultPlayerOperationsTest.EXISTING_USER, PlayCommandContext.of(EXISTING_PLAYABLE_ITEM_CONTEXT), CURRENT_DEVICE)
                .map(CurrentPlayerState::getPlayableItem)
                .as(StepVerifier::create)
                .assertNext(it -> {
                    assertThat(it.getId()).isEqualTo("cassie");
                    assertThat(it.getItemType()).isEqualTo(PlayableItemType.TRACK);
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionIfContextUriIsInvalid() {
        User user = prepareStateForUser();

        playerOperations.playOrResume(user, PlayCommandContext.of(INVALID_CONTEXT_URI), CURRENT_DEVICE)
                .as(StepVerifier::create)
                .expectError(ReasonCodeAwareMalformedContextUriException.class)
                .verify();
    }

    @Test
    void shouldContainReasonCodeIfContextUriIsInvalid() {
        String invalidContext = "sonata:invalid:cassie";
        User user = prepareStateForUser();

        StepVerifier.create(playerOperations.playOrResume(user, PlayCommandContext.of(invalidContext), CURRENT_DEVICE))
                .expectErrorMatches(err -> verifyReasonCode(err, "malformed_context_uri"))
                .verify();
    }

    @NotNull
    private User prepareStateForUser() {
        PlayerState playerState = PlayerStateFaker.create().user(existingUserEntity()).get();
        saveState(playerState); // prepare state for the user
        return DefaultPlayerOperationsTest.EXISTING_USER;
    }

    private static boolean verifyReasonCode(Throwable err, String expected) {
        if ( err instanceof ReasonCodeAware reasonCodeAware ) {
            return reasonCodeAware.getReasonCode().equals(expected);
        }
        return false;
    }
}
