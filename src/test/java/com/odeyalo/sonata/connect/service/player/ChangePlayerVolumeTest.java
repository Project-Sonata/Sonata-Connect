package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Volume;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.asserts.DevicesAssert;
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayerStateFaker;

import static org.assertj.core.api.Assertions.assertThat;
import static testing.factory.DefaultPlayerOperationsTestableBuilder.testableBuilder;

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

    // For single device - change volume
    // if multiple devices - change for active
    // if multiple devices and NO active device then error


    @Test
    void shouldReturnErrorIfThereIsNoDevices() {
        final PlayerStateEntity playingPlayerState = PlayerStateFaker.create()
                .user(existingUserEntity())
                .devicesEntity(DevicesEntity.empty())
                .get();

        final DefaultPlayerOperations testable = testableBuilder()
                .withState(playingPlayerState)
                .build();

        testable.changeVolume(EXISTING_USER, Volume.from(80))
                .as(StepVerifier::create)
                .expectError(NoActiveDeviceException.class)
                .verify();
    }

    @Test
    void shouldChangeVolumeForSingleDevice() {
        final PlayerStateEntity playingPlayerState = PlayerStateFaker.create()
                .user(existingUserEntity())
                .device(DeviceEntityFaker.createActiveDevice().get())
                .get();

        final DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.changeVolume(EXISTING_USER, Volume.from(40))
                .map(CurrentPlayerState::getDevices)
                .as(StepVerifier::create)
                .assertNext(devices -> DevicesAssert.forDevices(devices).peekFirst().volume(40))
                .verifyComplete();
    }

    @Test
    void shouldChangeVolumeOnlyForSingleActiveDeviceWhenMultipleDevicesAreConnected() {
        final PlayerStateEntity playingPlayerState = PlayerStateFaker.create()
                .user(existingUserEntity())
                .devicesEntity(DevicesEntity.just(
                        DeviceEntityFaker.createActiveDevice().setDeviceId("act1ve").get(),
                        DeviceEntityFaker.createInactiveDevice().get(),
                        DeviceEntityFaker.createInactiveDevice().get()
                ))
                .get();

        final DefaultPlayerOperations testable = testableBuilder().withState(playingPlayerState).build();

        testable.changeVolume(EXISTING_USER, Volume.from(60))
                .map(CurrentPlayerState::getDevices)
                .as(StepVerifier::create)
                .assertNext(devices -> DevicesAssert.forDevices(devices).peekById("act1ve").volume(60))
                .verifyComplete();
    }
}
