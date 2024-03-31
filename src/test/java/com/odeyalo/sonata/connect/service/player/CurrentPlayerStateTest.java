package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class CurrentPlayerStateTest extends DefaultPlayerOperationsTest {
    static final User EXISTING_USER = User.of("odeyalooo");

    @Test
    void shouldReturnNotNullExistedState() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldReturnIdForExistedState() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getId)
                .as(StepVerifier::create)
                .expectNext(state.getId())
                .verifyComplete();
    }

    @Test
    void shouldReturnRepeatState() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getRepeatState)
                .as(StepVerifier::create)
                .expectNext(state.getRepeatState())
                .verifyComplete();
    }

    @Test
    void shouldReturnShuffleState() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(state.getShuffleState())
                .verifyComplete();
    }

    @Test
    void shouldReturnProgressMs() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getProgressMs)
                .as(StepVerifier::create)
                .expectNext(state.getProgressMs())
                .verifyComplete();
    }

    @Test
    void shouldReturnPlayingType() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getPlayingType)
                .as(StepVerifier::create)
                .expectNext(state.getPlayingType())
                .verifyComplete();
    }

    @Test
    void shouldReturnCurrentlyPlayingItemId() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getPlayingItem)
                .map(PlayableItem::getId)
                .as(StepVerifier::create)
                .expectNext(state.getCurrentlyPlayingItem().getId())
                .verifyComplete();
    }

    @Test
    void shouldReturnCurrentlyPlayingItemType() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getPlayingItem)
                .map(PlayableItem::getItemType)
                .as(StepVerifier::create)
                .expectNext(state.getCurrentlyPlayingItem().getType())
                .verifyComplete();
    }
}
