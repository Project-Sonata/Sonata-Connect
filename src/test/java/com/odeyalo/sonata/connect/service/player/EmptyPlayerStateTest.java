package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class EmptyPlayerStateTest {
    static final User EXISTING_USER = User.of("odeyalooo");

    @Test
    void shouldReturnNotNull() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldReturnGeneratedId() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextMatches(it -> it.getId() > 0)
                .verifyComplete();
    }

    @Test
    void shouldReturnDefaultRepeatState() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getRepeatState)
                .as(StepVerifier::create)
                .expectNext(RepeatState.OFF)
                .verifyComplete();
    }

    @Test
    void shouldReturnShuffleState() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnDefaultProgressMs() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getProgressMs)
                .as(StepVerifier::create)
                .expectNext(-1L)
                .verifyComplete();
    }

    @Test
    void shouldReturnNullPlayingType() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextMatches(it -> it.getPlayingType() == null)
                .verifyComplete();
    }

    // We assume that empty state should not contain any track, episode, podcast, so on.
    @Test
    void shouldReturnNullPlayingItem() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextMatches(it -> it.getPlayableItem() == null)
                .verifyComplete();
    }
}
