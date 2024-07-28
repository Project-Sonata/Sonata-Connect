package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.Volume;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;

import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class ChangePlayerVolumeTest extends DefaultPlayerOperationsTest {

    @Test
    void shouldReturnStateWithChangedVolume() {
        final PlayerStateEntity playingPlayerState = PlayerStateFaker.create()
                .user(existingUserEntity())
                .get();

        final DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.changeVolume(EXISTING_USER, Volume.from(40))
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getVolume().asInt()).isEqualTo(40))
                .verifyComplete();
    }
    @Test

    void shouldSaveUpdatedState() {
        final PlayerStateEntity playingPlayerState = PlayerStateFaker.create()
                .user(existingUserEntity())
                .get();

        final DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.changeVolume(EXISTING_USER, Volume.from(40))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .assertNext(it -> assertThat(it.getVolume().asInt()).isEqualTo(40))
                .verifyComplete();
    }
}
