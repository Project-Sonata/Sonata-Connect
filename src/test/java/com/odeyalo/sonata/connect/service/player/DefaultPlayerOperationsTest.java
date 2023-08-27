package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.RepositoryDelegatePlayerStateStorage;
import com.odeyalo.sonata.connect.repository.storage.support.InMemory2PersistablePlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.factory.PersistablePlayerStateFactory;
import com.odeyalo.sonata.connect.service.support.mapper.Device2DeviceModelConverter;
import com.odeyalo.sonata.connect.service.support.mapper.DevicesToDevicesModelConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PersistablePlayerState2CurrentPlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayableItemEntity2PlayableItemConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;
import testing.faker.PlayerStateFaker;

import java.util.function.BiConsumer;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.SHUFFLE_DISABLED;
import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.SHUFFLE_ENABLED;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultPlayerOperationsTest {

    RepositoryDelegatePlayerStateStorage storage = new RepositoryDelegatePlayerStateStorage(new InMemoryPlayerStateRepository(), new InMemory2PersistablePlayerStateConverter());

    DefaultPlayerOperations playerOperations = new DefaultPlayerOperations(
            storage,
            new NullDeviceOperations(),
            new PersistablePlayerState2CurrentPlayerStateConverter(
                    new DevicesToDevicesModelConverter(new Device2DeviceModelConverter()),
                    new PlayableItemEntity2PlayableItemConverter())
    );

    @AfterEach
    void afterEach() {
        storage.clear().block();
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
        PersistablePlayerState playerState = saveState(createDisabledState());

        CurrentPlayerState updatedState = playerOperations.changeShuffle(createUser(playerState), SHUFFLE_ENABLED).block();

        assertThat(updatedState.getShuffleState()).isEqualTo(SHUFFLE_ENABLED);
    }

    @Test
    void changeShuffleToDisabled_andExpectShuffleToChange() {
        PersistablePlayerState playerState = saveState(createEnabledState());

        CurrentPlayerState updatedState = playerOperations.changeShuffle(createUser(playerState), SHUFFLE_DISABLED).block();

        assertThat(updatedState.getShuffleState()).isEqualTo(SHUFFLE_DISABLED);
    }

    @Test
    void shouldChangeNothingIfStateIsEqual() {
        PersistablePlayerState state = PlayerStateFaker.create().asPersistablePlayerState();
        PersistablePlayerState savedPlayerState = saveState(state);

        CurrentPlayerState updatedState = playerOperations.changeShuffle(createUser(savedPlayerState), state.getShuffleState()).block();

        assertThat(updatedState.getShuffleState()).isEqualTo(state.getShuffleState());
    }

    @Test
    @Disabled
    void shouldCreateEmptyStateIfUserNotAssociated() {

        User user = User.of("NakanoMiku");

        PersistablePlayerState expectedState = PersistablePlayerStateFactory.createEmpty(user);

        CurrentPlayerState playerState = playerOperations.createState(user).block();

        assertThat(playerState)
                .isEqualTo(expectedState);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
                    (expected, actual) -> assertThat(expected.getDevices()).isEqualTo(actual.getDevices())
            );
        }


        private void saveAndCompareActualWithExpected(BiConsumer<PersistablePlayerState, CurrentPlayerState> predicateConsumer) {
            PersistablePlayerState expected = PlayerStateFaker.create().asPersistablePlayerState();

            PersistablePlayerState playerState = saveState(expected);

            CurrentPlayerState actual = getCurrentPlayerState(playerState);

            predicateConsumer.accept(expected, actual);
        }

        @Nullable
        private CurrentPlayerState getCurrentPlayerState(PersistablePlayerState playerState) {
            return playerOperations.currentState(User.of(playerState.getUser().getId())).block();
        }
    }

    private static User createUser(PersistablePlayerState playerState) {
        return User.of(playerState.getUser().getId());
    }

    @Nullable
    private PersistablePlayerState saveState(PersistablePlayerState playerState) {
        return storage.save(playerState).block();
    }

    private PersistablePlayerState createEnabledState() {
        return PlayerStateFaker
                .create()
                .setShuffleState(SHUFFLE_ENABLED)
                .asPersistablePlayerState();
    }

    private static PersistablePlayerState createDisabledState() {
        return PlayerStateFaker
                .create()
                .setShuffleState(SHUFFLE_DISABLED)
                .asPersistablePlayerState();
    }

    static class NullDeviceOperations implements DeviceOperations {


        @Override
        public Mono<CurrentPlayerState> addDevice(User user, DeviceModel device) {
            return Mono.empty();
        }

        @NotNull
        @Override
        public Mono<Boolean> containsById(User user, String deviceId) {
            return Mono.empty();
        }

        @NotNull
        @Override
        public Mono<DevicesModel> getConnectedDevices(User user) {
            return Mono.empty();
        }
    }
}