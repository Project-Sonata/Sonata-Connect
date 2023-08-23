package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.RepositoryDelegatePlayerStateStorage;
import com.odeyalo.sonata.connect.repository.storage.support.InMemory2PersistablePlayerStateConverter;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import testing.faker.PlayerStateFaker;

import java.util.List;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.SHUFFLE_DISABLED;
import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.SHUFFLE_ENABLED;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultPlayerOperationsTest {

    RepositoryDelegatePlayerStateStorage storage = new RepositoryDelegatePlayerStateStorage(new InMemoryPlayerStateRepository(), new InMemory2PersistablePlayerStateConverter());

    DefaultPlayerOperations playerOperations = new DefaultPlayerOperations(
            storage,
            new NullDeviceOperations()
    );


    @Test
    void getStateForUser_andExpectStateToBeCreated() {
        User user = User.of("odeyalooo");

        CurrentPlayerState playerState = playerOperations.currentState(user).block();

        assertThat(playerState).isNotNull();
    }

    @Test
    void shouldReturnExistedState() {
        PersistablePlayerState expected = PlayerStateFaker.create().asPersistablePlayerState();

        PersistablePlayerState playerState = saveState(expected);

        CurrentPlayerState actual = getCurrentPlayerState(playerState);

        assertThat(actual).isNotNull();

        assertThat(expected.getId()).isEqualTo(actual.getId());
        assertThat(expected.getProgressMs()).isEqualTo(actual.getProgressMs());

    }

    @Nullable
    private CurrentPlayerState getCurrentPlayerState(PersistablePlayerState playerState) {
        return playerOperations.currentState(User.of(playerState.getUser().getId())).block();
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

    private static User createUser(PersistablePlayerState playerState) {
        return User.of(playerState.getUser().getId());
    }

    private PersistablePlayerState createEnabledState() {
        return PlayerStateFaker
                .create()
                .setShuffleState(SHUFFLE_ENABLED)
                .asPersistablePlayerState();
    }

    @Nullable
    private PersistablePlayerState saveState(PersistablePlayerState playerState) {
        return storage.save(playerState).block();
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
            return null;
        }

        @Override
        public Mono<Boolean> containsById(User user, String deviceId) {
            return null;
        }

        @Override
        public Mono<List<DeviceModel>> getConnectedDevices(User user) {
            return null;
        }
    }
}