package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.config.Converters;
import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.exception.DeviceNotFoundException;
import com.odeyalo.sonata.connect.exception.MultipleTargetDevicesNotSupportedException;
import com.odeyalo.sonata.connect.exception.SingleTargetDeactivationDeviceRequiredException;
import com.odeyalo.sonata.connect.exception.TargetDeviceRequiredException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.asserts.DevicesAssert;
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayerStateFaker;
import testing.faker.TargetDeactivationDevicesFaker;

import static org.assertj.core.api.Assertions.assertThat;
import static testing.condition.Conditions.reasonCodeEqual;

class SingleDeviceOnlyTransferPlaybackCommandHandlerDelegateTest {
    PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

    PlayerState2CurrentPlayerStateConverter playerStateConverter = new Converters().playerState2CurrentPlayerStateConverter();

    SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate testable = new SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate(playerStateRepository, playerStateConverter);

    final User USER = User.of("miku");
    final DeviceEntity ACTIVE_DEVICE = DeviceEntityFaker.createActiveDevice().get();
    final DeviceEntity INACTIVE_DEVICE = DeviceEntityFaker.createInactiveDevice().get();

    @BeforeEach
    void prepare() {
        final PlayerStateEntity entity = PlayerStateFaker.forUser(USER)
                .devicesEntity(DevicesEntity.just(ACTIVE_DEVICE, INACTIVE_DEVICE))
                .get();

        playerStateRepository.save(entity).block();
    }

    @AfterEach
    void tearDown() {
        playerStateRepository.clear().block();
    }

    @Test
    void transferAndExpectActiveDeviceToBecomeInactive() {
        final SwitchDeviceCommandArgs args = SwitchDeviceCommandArgs.ensurePlaybackStarted();
        final TargetDevices devices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

        testable.transferPlayback(USER, args, TargetDeactivationDevices.empty(), devices)
                .map(CurrentPlayerState::getDevices)
                .as(StepVerifier::create)
                .assertNext(it -> DevicesAssert.forDevices(it).peekById(ACTIVE_DEVICE.getId()).isIdle())
                .verifyComplete();
    }

    @Test
    void transferAndExpectTargetDeviceToBecomeActive() {
        final SwitchDeviceCommandArgs args = SwitchDeviceCommandArgs.ensurePlaybackStarted();
        final TargetDevices devices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

        testable.transferPlayback(USER, args, TargetDeactivationDevices.empty(), devices)
                .map(CurrentPlayerState::getDevices)
                .as(StepVerifier::create)
                .assertNext(it -> DevicesAssert.forDevices(it).peekById(INACTIVE_DEVICE.getId()).isActive())
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionIfDeviceIdIsInvalid() {
        final TargetDevices invalidDevices = TargetDevices.single(TargetDevice.of("not_exist"));

        testable.transferPlayback(USER, SwitchDeviceCommandArgs.noMatter(), TargetDeactivationDevices.empty(), invalidDevices)
                .as(StepVerifier::create)
                .expectErrorSatisfies(err -> {
                    assertThat(err)
                            .isInstanceOf(DeviceNotFoundException.class)
                            .hasMessage("Device with provided ID was not found!")
                            .is(reasonCodeEqual("device_not_found"));
                })
                .verify();
    }

    @Test
    void shouldThrowExceptionIfDeviceIdIsMoreThanOne() {
        final TargetDevices devices = TargetDevices.multiple(TargetDevice.of("something"), TargetDevice.of("something_else"));
        final SwitchDeviceCommandArgs args = SwitchDeviceCommandArgs.ensurePlaybackStarted();

        testable.transferPlayback(USER, args, TargetDeactivationDevices.empty(), devices)
                .as(StepVerifier::create)
                .expectErrorSatisfies(err -> {
                    assertThat(err)
                            .isInstanceOf(MultipleTargetDevicesNotSupportedException.class)
                            .hasMessage("One and only one deviceId should be provided. More than one is not supported now")
                            .is(reasonCodeEqual("multiple_devices_not_supported"));
                })
                .verify();
    }

    @Test
    void shouldThrowExceptionIfTargetDevicesSizeIsZero() {
        final TargetDevices devices = TargetDevices.empty();
        final SwitchDeviceCommandArgs args = SwitchDeviceCommandArgs.ensurePlaybackStarted();

        testable.transferPlayback(USER, args, TargetDeactivationDevices.empty(), devices)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(TargetDeviceRequiredException.class)
                            .hasMessage("Target device is required!")
                            .is(reasonCodeEqual("target_device_required"));
                })
                .verify();
    }

    @Test
    void shouldThrowExceptionIfTargetDeactivationDevicesSizeIsMoreThan1() {
        final TargetDeactivationDevices deactivationDevices = TargetDeactivationDevicesFaker.create(2).get();
        final TargetDevices targetDevices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

        testable.transferPlayback(USER, SwitchDeviceCommandArgs.ensurePlaybackStarted(), deactivationDevices, targetDevices)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertThat(error)
                            .isInstanceOf(SingleTargetDeactivationDeviceRequiredException.class)
                            .hasMessage("Single deactivation required but was received more or less!");
                })
                .verify();
    }
}