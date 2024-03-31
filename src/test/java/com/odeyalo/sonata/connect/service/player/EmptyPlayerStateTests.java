package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.RepeatState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.support.factory.PlayerStateFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class EmptyPlayerStateTests {
    PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();
    static final User EXISTING_USER = User.of("odeyalooo");

    @AfterEach
    void clear() {
        playerStateRepository.clear().block();
    }

    @Test
    void shouldReturnNotNull() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldReturnGeneratedId() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextMatches(it -> it.getId() > 0)
                .verifyComplete();
    }

    @Test
    void shouldReturnDefaultRepeatState() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getRepeatState)
                .as(StepVerifier::create)
                .expectNext(RepeatState.OFF)
                .verifyComplete();
    }

    @Test
    void shouldReturnShuffleState() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getShuffleState)
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnProgressMs() {
        createEmptyPlayerStateAndAssert((expected, actual) -> assertThat(actual.getProgressMs()).isEqualTo(expected.getProgressMs()));
    }

    @Test
    void shouldReturnPlayingType() {
        createEmptyPlayerStateAndAssert((expected, actual) -> assertThat(actual.getPlayingType()).isEqualTo(expected.getPlayingType()));
    }

    @Test
    void shouldReturnPlayingItem() {
        // We assume that empty state should not contain any track, episode, podcast, so on.
        createEmptyPlayerStateAndAssert((expected, actual) -> assertThat(actual.getPlayingItem()).isNull());
    }

    private void createEmptyPlayerStateAndAssert(BiConsumer<PlayerState, CurrentPlayerState> predicateConsumer) {
        PlayerState expected = PlayerStateFactory.createEmpty(EXISTING_USER);

        CurrentPlayerState actual = testableBuilder().build().createState(EXISTING_USER).block();

        predicateConsumer.accept(expected, actual);
    }
}
