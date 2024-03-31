package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.HardcodedContextUriParser;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
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
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.asserts.PlayableItemEntityAssert;
import testing.faker.PlayerStateFaker;
import testing.stub.NullDeviceOperations;

import java.util.Objects;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

class CurrentlyPlayingPlayerStateTests {
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
        saveAndAssert((expected, actual) -> assertThat(expected.getPlayingType()).isEqualTo(actual.getCurrentlyPlayingType()));
    }

    private void saveAndAssert(BiConsumer<PlayerState, CurrentlyPlayingPlayerState> predicateConsumer) {
        PlayerState expected = playingActivePlayerState();

        PlayerState playerState = saveState(expected);

        CurrentlyPlayingPlayerState currentlyPlayingState = getCurrentlyPlayingState(playerState);

        predicateConsumer.accept(playerState, currentlyPlayingState);
    }

    private static PlayerState playingActivePlayerState() {
        return PlayerStateFaker.create().playing(true).user(existingUserEntity()).get();
    }

    @Nullable
    private CurrentlyPlayingPlayerState getCurrentlyPlayingState(PlayerState playerState) {
        return getCurrentlyPlayingPlayerState(User.of(playerState.getUser().getId()));
    }

    @Nullable
    private CurrentlyPlayingPlayerState getCurrentlyPlayingPlayerState(User user) {
        return playerOperations.currentlyPlayingState(user).block();
    }

    protected static UserEntity existingUserEntity() {
        return UserEntity.builder()
                .id(EXISTING_USER.getId())
                .build();
    }

    @NotNull
    protected PlayerState saveState(PlayerState playerState) {
        return Objects.requireNonNull(playerStateRepository.save(playerState).block());
    }
}
