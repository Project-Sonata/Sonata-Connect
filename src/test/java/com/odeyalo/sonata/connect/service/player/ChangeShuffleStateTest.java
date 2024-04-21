package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.SHUFFLE_DISABLED;
import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.SHUFFLE_ENABLED;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class ChangeShuffleStateTest extends DefaultPlayerOperationsTest {
    static final User EXISTING_USER = User.of("odeyalooo");

    @Test
    void changeShuffleToEnabled_andExpectShuffleToChange() {
        PlayerStateEntity withShuffleDisabled = playerStateWithShuffleDisabled();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(withShuffleDisabled)
                .build();

        testable.changeShuffle(EXISTING_USER, SHUFFLE_ENABLED)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(SHUFFLE_ENABLED)
                .verifyComplete();
    }

    @Test
    void changeShuffleToDisabled_andExpectShuffleToChange() {
        PlayerStateEntity withShuffleDisabled = playerStateWithShuffleEnabled();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(withShuffleDisabled)
                .build();

        testable.changeShuffle(EXISTING_USER, SHUFFLE_DISABLED)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(SHUFFLE_DISABLED)
                .verifyComplete();
    }

    @Test
    void shouldChangeNothingIfShuffleStateIsEqual() {
        PlayerStateEntity state = existingPlayerState();
        boolean expectedShuffleState = state.getShuffleState();

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
                .shuffleState(SHUFFLE_ENABLED)
                .get();
    }

    private static PlayerStateEntity playerStateWithShuffleDisabled() {
        UserEntity user = existingUserEntity();

        return PlayerStateFaker
                .create()
                .shuffleState(SHUFFLE_DISABLED)
                .user(user)
                .get();
    }
}
