package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.ShuffleMode;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static com.odeyalo.sonata.connect.model.ShuffleMode.ENABLED;
import static com.odeyalo.sonata.connect.model.ShuffleMode.OFF;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class ChangeShuffleStateTest extends DefaultPlayerOperationsTest {
    static final User EXISTING_USER = User.of("odeyalooo");

    @Test
    void changeShuffleToEnabled_andExpectShuffleToChange() {
        PlayerStateEntity withShuffleDisabled = playerStateWithShuffleDisabled();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(withShuffleDisabled)
                .build();

        testable.changeShuffle(EXISTING_USER, ENABLED)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(ENABLED)
                .verifyComplete();
    }

    @Test
    void changeShuffleToDisabled_andExpectShuffleToChange() {
        PlayerStateEntity withShuffleDisabled = playerStateWithShuffleEnabled();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(withShuffleDisabled)
                .build();

        testable.changeShuffle(EXISTING_USER, OFF)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(OFF)
                .verifyComplete();
    }

    @Test
    void shouldChangeNothingIfShuffleStateIsEqual() {
        PlayerStateEntity state = existingPlayerState();
        ShuffleMode expectedShuffleState = state.getShuffleState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();


        testable.changeShuffle(EXISTING_USER, expectedShuffleState)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(expectedShuffleState)
                .verifyComplete();

    }

    private PlayerStateEntity playerStateWithShuffleEnabled() {
        UserEntity user = existingUserEntity();

        return PlayerStateFaker
                .create()
                .user(user)
                .shuffleState(ENABLED)
                .get();
    }

    private static PlayerStateEntity playerStateWithShuffleDisabled() {
        UserEntity user = existingUserEntity();

        return PlayerStateFaker
                .create()
                .shuffleState(OFF)
                .user(user)
                .get();
    }
}
