package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayerStateFaker;

import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class PausePlaybackCommandTest extends DefaultPlayerOperationsTest {

    @Test
    void shouldReturnUpdatedPlayerState() {
        PlayerStateEntity playerState = activePlayerState();
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
        PlayerStateEntity playerState = activePlayerState();
        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .build();

        testable.pause(EXISTING_USER)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.isPlaying()).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorIfNoActiveDevice() {
        PlayerStateEntity playerState = activePlayerStateWithNoActiveDevice();
        DefaultPlayerOperations testable = testableBuilder()
                .withState(playerState)
                .build();

        testable.pause(EXISTING_USER)
                .as(StepVerifier::create)
                .expectError(NoActiveDeviceException.class)
                .verify();
    }

    private PlayerStateEntity activePlayerStateWithNoActiveDevice() {
        UserEntity userEntity = existingUserEntity();
        DeviceEntity inactiveDevice = DeviceEntityFaker.createInactiveDevice().get();
        return PlayerStateFaker.create().playing(true).user(userEntity).device(inactiveDevice).get();
    }

    private static PlayerStateEntity activePlayerState() {
        UserEntity userEntity = existingUserEntity();
        return PlayerStateFaker.create().playing(true).user(userEntity).get();
    }
}
