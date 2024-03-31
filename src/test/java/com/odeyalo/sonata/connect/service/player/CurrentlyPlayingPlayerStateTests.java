package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class CurrentlyPlayingPlayerStateTests {
    static final User EXISTING_USER = User.of("odeyalooo");

    @Test
    void shouldReturnShuffleState() {
        PlayerState playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(playingPlayerState.getShuffleState())
                .verifyComplete();
    }

    @Test
    void shouldReturnRepeatState() {
        PlayerState playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getRepeatState)
                .as(StepVerifier::create)
                .expectNext(playingPlayerState.getRepeatState())
                .verifyComplete();
    }

    @Test
    void shouldReturnPlayableItemId() {
        PlayerState playingPlayerState = playingActivePlayerState();
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
        PlayerState playingPlayerState = playingActivePlayerState();
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
        PlayerState playingPlayerState = playingActivePlayerState();
        DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getPlayingType)
                .as(StepVerifier::create)
                .expectNext(playingPlayerState.getCurrentlyPlayingType())
                .verifyComplete();
    }

    private static PlayerState playingActivePlayerState() {
        return PlayerStateFaker.create().playing(true).user(existingUserEntity()).get();
    }

    protected static UserEntity existingUserEntity() {
        return UserEntity.builder()
                .id(EXISTING_USER.getId())
                .build();
    }
}
