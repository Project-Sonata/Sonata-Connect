package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class DefaultPlayerOperationsTest {

    public static final User EXISTING_USER = User.of("odeyalooo");

    @Test
    void getStateForUser_andExpectStateToBeCreated() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    protected static PlayerState existingPlayerState() {
        UserEntity existingUserEntity = existingUserEntity();
        return PlayerStateFaker.create().user(existingUserEntity).get();
    }

    protected static UserEntity existingUserEntity() {
        return UserEntity.builder()
                .id(EXISTING_USER.getId())
                .build();
    }
}