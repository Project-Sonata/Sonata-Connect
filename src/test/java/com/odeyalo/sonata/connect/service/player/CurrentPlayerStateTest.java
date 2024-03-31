package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.HardcodedContextUriParser;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.PlayerStateUpdatePlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.support.HardcodedPlayableItemResolver;
import com.odeyalo.sonata.connect.service.player.support.validation.HardcodedPlayCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.support.mapper.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.faker.PlayerStateFaker;
import testing.stub.NullDeviceOperations;

import java.util.Objects;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class CurrentPlayerStateTest {

    public static final User EXISTING_USER = User.of("odeyalooo");
    PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

    PlayerState2CurrentPlayerStateConverter converter = new PlayerState2CurrentPlayerStateConverter(
            new DevicesEntity2DevicesConverter(new DeviceEntity2DeviceConverter()),
            new PlayableItemEntity2PlayableItemConverter());

    DefaultPlayerOperations playerOperations = new DefaultPlayerOperations(
            playerStateRepository,
            new NullDeviceOperations(),
            converter,
            new PlayerStateUpdatePlayCommandHandlerDelegate(playerStateRepository, converter,
                    new HardcodedContextUriParser(),
                    new HardcodedPlayableItemResolver(),
                    new HardcodedPlayCommandPreExecutingIntegrityValidator()),
            new CurrentPlayerState2CurrentlyPlayingPlayerStateConverter());


    @AfterEach
    void afterEach() {
        playerStateRepository.clear().block();
    }

    @Test
    void shouldReturnNotNullExistedState() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldReturnIdForExistedState() {
        PlayerState state = existingPlayerState();

        DefaultPlayerOperations testable = testableBuilder()
                .withState(state)
                .build();

        testable.currentState(EXISTING_USER)
                .map(CurrentPlayerState::getId)
                .as(StepVerifier::create)
                .expectNextCount(state.getId())
                .verifyComplete();
    }

    @Test
    void shouldReturnRepeatState() {
        saveAndCompareActualWithExpected(
                (expected, actual) -> assertThat(actual.getRepeatState()).isEqualTo(expected.getRepeatState()));
    }

    @Test
    void shouldReturnShuffleState() {
        saveAndCompareActualWithExpected(
                (expected, actual) -> assertThat(expected.getShuffleState()).isEqualTo(actual.getShuffleState())
        );
    }


    @Test
    void shouldReturnProgressMs() {
        saveAndCompareActualWithExpected(
                (expected, actual) -> assertThat(expected.getProgressMs()).isEqualTo(actual.getProgressMs())
        );
    }

    @Test
    void shouldReturnPlayingType() {
        saveAndCompareActualWithExpected(
                (expected, actual) -> assertThat(expected.getPlayingType()).isEqualTo(actual.getPlayingType())
        );
    }

    @Test
    void shouldReturnCurrentlyPlayingItem() {
        saveAndCompareActualWithExpected(
                (expected, actual) -> {
                    PlayableItemEntity expectedItem = expected.getCurrentlyPlayingItem();
                    PlayableItem actualItem = actual.getPlayingItem();
                    assertThat(expectedItem.getId()).isEqualTo(actualItem.getId());
                    assertThat(expectedItem.getType()).isEqualTo(actualItem.getItemType());
                }
        );
    }

    private void saveAndCompareActualWithExpected(BiConsumer<PlayerState, CurrentPlayerState> predicateConsumer) {
        PlayerState expected = existingPlayerState();

        PlayerState playerState = saveState(expected);

        CurrentPlayerState actual = getCurrentPlayerState(playerState);

        predicateConsumer.accept(expected, actual);
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

    @Nullable
    private CurrentPlayerState getCurrentPlayerState(PlayerState playerState) {
        return playerOperations.currentState(User.of(playerState.getUser().getId())).block();
    }


    @NotNull
    protected PlayerState saveState(PlayerState playerState) {
        return Objects.requireNonNull(playerStateRepository.save(playerState).block());
    }

}
