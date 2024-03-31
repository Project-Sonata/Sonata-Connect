package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.HardcodedContextUriParser;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.PlayerStateUpdatePlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.support.HardcodedPlayableItemResolver;
import com.odeyalo.sonata.connect.service.player.support.validation.HardcodedPlayCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.support.factory.PlayerStateFactory;
import com.odeyalo.sonata.connect.service.support.mapper.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import reactor.test.StepVerifier;
import testing.stub.NullDeviceOperations;
import testing.asserts.PlayableItemEntityAssert;
import testing.faker.PlayerStateFaker;

import java.util.Objects;
import java.util.function.BiConsumer;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.*;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

class DefaultPlayerOperationsTest {

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
    void getStateForUser_andExpectStateToBeCreated() {
        DefaultPlayerOperations testable = testableBuilder().build();

        testable.currentState(EXISTING_USER)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

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

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class EmptyPlayerStateTests {
        @AfterEach
        void clear() {
            playerStateRepository.clear().block();
        }

        @Test
        void shouldReturnNotNull() {
            createEmptyPlayerStateAndAssert((expected, actual) -> assertThat(actual).isNotNull());
        }

        @Test
        void shouldReturnId() {
            createEmptyPlayerStateAndAssert((expected, actual) -> assertThat(actual.getId()).isNotNull().isPositive());
        }

        @Test
        void shouldReturnRepeatState() {
            createEmptyPlayerStateAndAssert((expected, actual) -> assertThat(actual.getRepeatState()).isEqualTo(expected.getRepeatState()));
        }

        @Test
        void shouldReturnShuffleState() {
            createEmptyPlayerStateAndAssert((expected, actual) -> assertThat(actual.getShuffleState()).isEqualTo(expected.getShuffleState()));
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

            CurrentPlayerState actual = playerOperations.createState(EXISTING_USER).block();

            predicateConsumer.accept(expected, actual);
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class CurrentPlayerStateTest {
        @Test
        void shouldReturnNotNullExistedState() {
            saveAndCompareActualWithExpected((
                    (expected, actual) -> assertThat(actual).isNotNull())
            );
        }

        @Test
        void shouldReturnIdForExistedState() {
            saveAndCompareActualWithExpected((
                    (expected, actual) -> assertThat(actual.getId()).isEqualTo(expected.getId()))
            );
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

        @Nullable
        private CurrentPlayerState getCurrentPlayerState(PlayerState playerState) {
            return playerOperations.currentState(User.of(playerState.getUser().getId())).block();
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class CurrentlyPlayingPlayerStateTests {

        @Test
        void shouldReturnShuffleState() {
            saveAndAssert((expected, actual) -> assertThat(expected.getShuffleState()).isEqualTo(actual.getShuffleState()));
        }

        @Test
        void shouldReturnRepeatState() {
            saveAndAssert((expected, actual) -> assertThat(expected.getRepeatState()).isEqualTo(actual.getRepeatState()));
        }

        @Test
        void shouldReturnPlayableItem() {
            saveAndAssert((expected, actual) -> {

                PlayableItemEntityAssert.forEntity(expected.getCurrentlyPlayingItem())
                        .id().isEqualTo(actual.getPlayableItem().getId());

                PlayableItemEntityAssert.forEntity(expected.getCurrentlyPlayingItem())
                        .entityType().isEqualTo(actual.getPlayableItem().getItemType());
            });
        }

        @Test
        void shouldReturnPlayingType() {
            saveAndAssert((expected, actual) -> assertThat(expected.getPlayingType()).isEqualTo(actual.getCurrentlyPlayingType()));
        }

        private void saveAndAssert(BiConsumer<PlayerState, CurrentlyPlayingPlayerState> predicateConsumer) {
            PlayerState expected = PlayerStateFaker.create().playing(true).get();

            PlayerState playerState = saveState(expected);

            CurrentlyPlayingPlayerState currentlyPlayingState = getCurrentlyPlayingState(playerState);

            predicateConsumer.accept(playerState, currentlyPlayingState);
        }

        @Nullable
        private CurrentlyPlayingPlayerState getCurrentlyPlayingState(PlayerState playerState) {
            return getCurrentlyPlayingPlayerState(User.of(playerState.getUser().getId()));
        }

        @Nullable
        private CurrentlyPlayingPlayerState getCurrentlyPlayingPlayerState(User user) {
            return playerOperations.currentlyPlayingState(user).block();
        }
    }

    @NotNull
    protected PlayerState saveState(PlayerState playerState) {
        return Objects.requireNonNull(playerStateRepository.save(playerState).block());
    }

    private PlayerState playerStateWithShuffleEnabled() {
        UserEntity user = existingUserEntity();

        return PlayerStateFaker
                .create()
                .user(user)
                .shuffleState(SHUFFLE_ENABLED)
                .get();
    }

    protected static UserEntity existingUserEntity() {
        return UserEntity.builder()
                .id(EXISTING_USER.getId())
                .build();
    }

    private static PlayerState playerStateWithShuffleDisabled() {
        UserEntity user = existingUserEntity();

        return PlayerStateFaker
                .create()
                .shuffleState(SHUFFLE_DISABLED)
                .user(user)
                .get();
    }

}