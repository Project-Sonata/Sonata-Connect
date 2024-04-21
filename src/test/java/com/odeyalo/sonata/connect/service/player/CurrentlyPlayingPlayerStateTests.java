package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class CurrentlyPlayingPlayerStateTests extends DefaultPlayerOperationsTest {
    static final User EXISTING_USER = User.of("odeyalooo");

    @Test
    void shouldReturnShuffleState() {
        PlayerStateEntity playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(playingPlayerState.getShuffleState())
                .verifyComplete();
    }

    @Test
    void shouldReturnRepeatState() {
        PlayerStateEntity playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getRepeatState)
                .as(StepVerifier::create)
                .expectNext(playingPlayerState.getRepeatState())
                .verifyComplete();
    }

    @Test
    void shouldReturnPlayableItemId() {
        PlayerStateEntity playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getPlayableItem)
                .map(PlayableItem::getId)
                .as(StepVerifier::create)
                .expectNext(playingPlayerState.getCurrentlyPlayingItem().getId())
                .verifyComplete();
    }

    @Test
    void shouldReturnPlayableItemType() {
        PlayerStateEntity playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getPlayableItem)
                .map(PlayableItem::getItemType)
                .as(StepVerifier::create)
                .expectNext(playingPlayerState.getCurrentlyPlayingItem().getType())
                .verifyComplete();
    }

    @Test
    void shouldReturnPlayingType() {
        PlayerStateEntity playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getPlayingType)
                .as(StepVerifier::create)
                .expectNext(playingPlayerState.getCurrentlyPlayingType())
                .verifyComplete();
    }

    @Test
    void shouldReturnProgressMs() {
        PlayerStateEntity playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextMatches(it -> it.getProgressMs() >= 0)
                .verifyComplete();
    }

    private static PlayerStateEntity playingActivePlayerState() {
        return PlayerStateFaker.create().playing(true).user(existingUserEntity()).get();
    }
}
