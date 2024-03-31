package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.SHUFFLE_DISABLED;
import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.SHUFFLE_ENABLED;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class ChangeShuffleStateTest {
    static final User EXISTING_USER = User.of("odeyalooo");

    @Test
    void changeShuffleToEnabled_andExpectShuffleToChange() {
        PlayerState withShuffleDisabled = playerStateWithShuffleDisabled();

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
        PlayerState withShuffleDisabled = playerStateWithShuffleEnabled();

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
        PlayerState state = existingPlayerState();
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

    protected static PlayerState existingPlayerState() {
        UserEntity existingUserEntity = existingUserEntity();
        return PlayerStateFaker.create().user(existingUserEntity).get();
    }

    private PlayerState playerStateWithShuffleEnabled() {
        UserEntity user = existingUserEntity();

        return PlayerStateFaker
                .create()
                .user(user)
                .shuffleState(SHUFFLE_ENABLED)
                .get();
    }

    private static PlayerState playerStateWithShuffleDisabled() {
        UserEntity user = existingUserEntity();

        return PlayerStateFaker
                .create()
                .shuffleState(SHUFFLE_DISABLED)
                .user(user)
                .get();
    }

    protected static UserEntity existingUserEntity() {
        return UserEntity.builder()
                .id(EXISTING_USER.getId())
                .build();
    }
}
