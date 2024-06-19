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
import com.odeyalo.sonata.connect.model.Devices;
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
import testing.asserts.DevicesAssert;
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayerStateFaker;
import testing.faker.TargetDeactivationDevicesFaker;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static testing.condition.Conditions.reasonCodeEqual;

class SingleDeviceOnlyTransferPlaybackCommandHandlerDelegateTest {
    PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

    PlayerState2CurrentPlayerStateConverter playerStateConverter = new Converters().playerState2CurrentPlayerStateConverter();

    SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate operations = new SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate(playerStateRepository, playerStateConverter);

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
        SwitchDeviceCommandArgs args = SwitchDeviceCommandArgs.ensurePlaybackStarted();
        TargetDevices devices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

        CurrentPlayerState updatedPlayerState = operations.transferPlayback(USER, args, TargetDeactivationDevices.empty(), devices).block();

        Devices actualDevices = updatedPlayerState.getDevices();

        DevicesAssert.forDevices(actualDevices)
                .peekById(ACTIVE_DEVICE.getId()).inactive();
    }

    @Test
    void transferAndExpectTargetDeviceToBecomeActive() {
        SwitchDeviceCommandArgs args = SwitchDeviceCommandArgs.ensurePlaybackStarted();
        TargetDevices devices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

        CurrentPlayerState updatedPlayerState = operations.transferPlayback(USER, args, TargetDeactivationDevices.empty(), devices).block();

        Devices actualDevices = updatedPlayerState.getDevices();

        DevicesAssert.forDevices(actualDevices)
                .peekById(INACTIVE_DEVICE.getId()).active();
    }

    @Test
    void shouldThrowExceptionIfDeviceIdIsInvalid() {
        TargetDevices invalidDevices = TargetDevices.single(TargetDevice.of("not_exist"));

        assertThatThrownBy(() -> operations.transferPlayback(USER, SwitchDeviceCommandArgs.noMatter(), TargetDeactivationDevices.empty(), invalidDevices).block())
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessage("Device with provided ID was not found!")
                .is(reasonCodeEqual("device_not_found"));
    }

    @Test
    void shouldThrowExceptionIfDeviceIdIsMoreThanOne() {
        TargetDevices devices = TargetDevices.multiple(TargetDevice.of("something"), TargetDevice.of("something_else"));
        SwitchDeviceCommandArgs args = SwitchDeviceCommandArgs.ensurePlaybackStarted();

        assertThatThrownBy(() -> operations.transferPlayback(USER, args, TargetDeactivationDevices.empty(), devices).block())
                .isInstanceOf(MultipleTargetDevicesNotSupportedException.class)
                .hasMessage("One and only one deviceId should be provided. More than one is not supported now")
                .is(reasonCodeEqual("multiple_devices_not_supported"));
    }

    @Test
    void shouldThrowExceptionIfTargetDevicesSizeIsZero() {
        TargetDevices devices = TargetDevices.empty();
        SwitchDeviceCommandArgs args = SwitchDeviceCommandArgs.ensurePlaybackStarted();

        assertThatThrownBy(() -> operations.transferPlayback(USER, args, TargetDeactivationDevices.empty(), devices).block())
                .isInstanceOf(TargetDeviceRequiredException.class)
                .hasMessage("Target device is required!")
                .is(reasonCodeEqual("target_device_required"));
    }

    @Test
    void shouldThrowExceptionIfTargetDeactivationDevicesSizeIsMoreThan1() {
        TargetDeactivationDevices deactivationDevices = TargetDeactivationDevicesFaker.create(2).get();
        TargetDevices targetDevices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

        assertThatThrownBy(() -> operations.transferPlayback(USER, SwitchDeviceCommandArgs.ensurePlaybackStarted(), deactivationDevices, targetDevices).block())
                .isInstanceOf(SingleTargetDeactivationDeviceRequiredException.class)
                .hasMessage("Single deactivation required but was received more or less!");
    }
}