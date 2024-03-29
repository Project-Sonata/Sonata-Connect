package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.HardcodedContextUriParser;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.entity.UserEntity;
import com.odeyalo.sonata.connect.exception.ReasonCodeAware;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.PlayerStateUpdatePlayCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.support.HardcodedPlayableItemResolver;
import com.odeyalo.sonata.connect.service.player.support.validation.HardcodedPlayCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.factory.PlayerStateFactory;
import com.odeyalo.sonata.connect.service.support.mapper.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testing.asserts.PlayableItemEntityAssert;
import testing.faker.PlayerStateFaker;

import java.util.Objects;
import java.util.function.BiConsumer;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

class DefaultPlayerOperationsTest {

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
        User user = User.of("odeyalooo");

        CurrentPlayerState playerState = playerOperations.currentState(user).block();

        assertThat(playerState).isNotNull();
    }

    @Test
    void shouldReturnCurrentEmptyState() {
        User user = User.of("Mikuuuu");
        CurrentPlayerState expectedState = playerOperations.createState(user).block();
        CurrentPlayerState actualState = playerOperations.currentState(user).block();

        assertThat(actualState).isEqualTo(expectedState);
    }

    @Test
    void changeShuffleToEnabled_andExpectShuffleToChange() {
        PlayerState playerState = saveState(createDisabledState());

        CurrentPlayerState updatedState = playerOperations.changeShuffle(createUser(playerState), SHUFFLE_ENABLED).block();

        assertThat(updatedState.getShuffleState()).isEqualTo(SHUFFLE_ENABLED);
    }

    @Test
    void changeShuffleToDisabled_andExpectShuffleToChange() {
        PlayerState playerState = saveState(createEnabledState());

        CurrentPlayerState updatedState = playerOperations.changeShuffle(createUser(playerState), SHUFFLE_DISABLED).block();

        assertThat(updatedState.getShuffleState()).isEqualTo(SHUFFLE_DISABLED);
    }

    @Test
    void shouldChangeNothingIfStateIsEqual() {
        PlayerState state = PlayerStateFaker.create().get();
        PlayerState savedPlayerState = saveState(state);

        CurrentPlayerState updatedState = playerOperations.changeShuffle(createUser(savedPlayerState), state.getShuffleState()).block();

        assertThat(updatedState.getShuffleState()).isEqualTo(state.getShuffleState());
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
            User user = User.of("NakanoMiku");

            PlayerState expected = PlayerStateFactory.createEmpty(user);

            CurrentPlayerState actual = playerOperations.createState(user).block();

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

        @Test
        @Disabled("Disabled because I am too lazy to assert it now, will do it later")
        void shouldReturnDevices() {
            saveAndCompareActualWithExpected(
                    (expected, actual) -> assertThat(expected.getDevicesEntity()).isEqualTo(actual.getDevices())
            );
        }


        private void saveAndCompareActualWithExpected(BiConsumer<PlayerState, CurrentPlayerState> predicateConsumer) {
            PlayerState expected = PlayerStateFaker.create().get();

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
    class PlayResumeCommandTests {

        @AfterEach
        void cleanup() {
            playerStateRepository.clear().block();
        }

        @Test
        void shouldUpdateState() {
            String validContext = "sonata:track:cassie";
            User user = prepareStateForUser();
            CurrentPlayerState updatedState = playerOperations.playOrResume(user, PlayCommandContext.of(validContext), CURRENT_DEVICE).block();

            assertThat(updatedState.getPlayingItem().getId()).isEqualTo("cassie");
            assertThat(updatedState.getPlayingItem().getItemType()).isEqualTo(PlayableItemType.TRACK);
        }

        @Test
        void shouldThrowExceptionIfContextUriIsInvalid() {
            String invalidContext = "sonata:invalid:cassie";
            User user = prepareStateForUser();

            StepVerifier.create(playerOperations.playOrResume(user, PlayCommandContext.of(invalidContext), CURRENT_DEVICE))
                    .expectError(ReasonCodeAwareMalformedContextUriException.class)
                    .verify();
        }

        @Test
        void shouldContainReasonCodeIfContextUriIsInvalid() {
            String invalidContext = "sonata:invalid:cassie";
            User user = prepareStateForUser();

            StepVerifier.create(playerOperations.playOrResume(user, PlayCommandContext.of(invalidContext), CURRENT_DEVICE))
                    .expectErrorMatches(err -> verifyReasonCode(err, "malformed_context_uri"))
                    .verify();
        }

        @NotNull
        private User prepareStateForUser() {
            User user = User.of("miku");
            PlayerState playerState = PlayerStateFaker.create().user(UserEntity.builder().id(user.getId()).build()).get();
            saveState(playerState); // prepare state for the user
            return user;
        }

        private static boolean verifyReasonCode(Throwable err, String expected) {
            if ( err instanceof ReasonCodeAware reasonCodeAware ) {
                return reasonCodeAware.getReasonCode().equals(expected);
            }
            return false;
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

    private static User createUser(PlayerState playerState) {
        return User.of(playerState.getUser().getId());
    }

    @NotNull
    private PlayerState saveState(PlayerState playerState) {
        return Objects.requireNonNull(playerStateRepository.save(playerState).block());
    }

    private PlayerState createEnabledState() {
        return PlayerStateFaker
                .create()
                .shuffleState(SHUFFLE_ENABLED)
                .get();
    }

    private static PlayerState createDisabledState() {
        return PlayerStateFaker
                .create()
                .shuffleState(SHUFFLE_DISABLED)
                .get();
    }

    static class NullDeviceOperations implements DeviceOperations {

        @NotNull
        @Override
        public Mono<CurrentPlayerState> addDevice(User user, Device device) {
            return Mono.empty();
        }

        @NotNull
        @Override
        public Mono<Boolean> containsById(User user, String deviceId) {
            return Mono.empty();
        }

        @NotNull
        @Override
        public Mono<CurrentPlayerState> transferPlayback(User user, SwitchDeviceCommandArgs args, TargetDeactivationDevices deactivationDevices, TargetDevices targetDevices) {
            return Mono.empty();
        }

        @NotNull
        @Override
        public Mono<CurrentPlayerState> disconnectDevice(User user, DisconnectDeviceArgs args) {
            return Mono.empty();
        }

        @NotNull
        @Override
        public Mono<Devices> getConnectedDevices(User user) {
            return Mono.empty();
        }
    }
}