package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;
import static org.assertj.core.api.Assertions.assertThat;
class PausePlaybackCommandTest extends DefaultPlayerOperationsTest {

    @Test
    void shouldReturnUpdatedPlayerState() {
        PlayerState playerState = activePlayerState();
        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .build();

        testable.pause(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
    @Test
    void shouldReturnPlayingSetToFalse() {
        PlayerState playerState = activePlayerState();
        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .build();

        testable.pause(EXISTING_USER)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.isPlaying()).isFalse())
                .verifyComplete();
    }

    private static PlayerState activePlayerState() {
        UserEntity userEntity = existingUserEntity();
        return PlayerStateFaker.create().playing(true).user(userEntity).get();
    }
}
