package com.odeyalo.sonata.connect.service.player;

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
import com.odeyalo.sonata.connect.service.player.handler.SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import reactor.test.StepVerifier;
import testing.asserts.DevicesAssert;
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayerStateFaker;
import testing.faker.TargetDeactivationDevicesFaker;

import java.util.List;
import java.util.Objects;

import static com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs.ensurePlaybackStarted;
import static com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices.empty;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static testing.condition.Conditions.reasonCodeEqual;

class DefaultStorageDeviceOperationsTest {

    PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

    PlayerState2CurrentPlayerStateConverter playerStateConverter = new Converters().playerState2CurrentPlayerStateConverter();

    DefaultStorageDeviceOperations operations = new DefaultStorageDeviceOperations(playerStateRepository,
            new SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate(playerStateRepository, playerStateConverter), playerStateConverter,
            new DevicesEntity2DevicesConverter(new DeviceEntity2DeviceConverterImpl()),
            new Device2DeviceEntityConverterImpl());

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

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class TransferPlaybackTests {

        @Test
        void transferAndExpectActiveDeviceToBecomeInactive() {
            SwitchDeviceCommandArgs args = ensurePlaybackStarted();
            TargetDevices devices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

            CurrentPlayerState updatedPlayerState = operations.transferPlayback(USER, args, empty(), devices).block();

            Devices actualDevices = updatedPlayerState.getDevices();

            DevicesAssert.forDevices(actualDevices)
                    .peekById(ACTIVE_DEVICE.getId()).inactive();
        }

        @Test
        void transferAndExpectTargetDeviceToBecomeActive() {
            SwitchDeviceCommandArgs args = ensurePlaybackStarted();
            TargetDevices devices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

            CurrentPlayerState updatedPlayerState = operations.transferPlayback(USER, args, empty(), devices).block();

            Devices actualDevices = updatedPlayerState.getDevices();

            DevicesAssert.forDevices(actualDevices)
                    .peekById(INACTIVE_DEVICE.getId()).active();
        }

        @Test
        void shouldRemoveExistingDevice() {
            String disconnectTargetId = INACTIVE_DEVICE.getId();

            operations.disconnectDevice(USER, DisconnectDeviceArgs.withDeviceId(disconnectTargetId))
                    .map(CurrentPlayerState::getDevices)
                    .as(StepVerifier::create)
                    .expectNextMatches(devices -> devices.stream().noneMatch(device -> Objects.equals(device.getDeviceId(), disconnectTargetId)))
                    .verifyComplete();
        }

        @Test
        void shouldNotChangeStateIfDeviceNotExist() {
            String disconnectTargetId = "not_existing";
            Devices connectedDevices = operations.getConnectedDevices(USER).block();

            //noinspection DataFlowIssue
            operations.disconnectDevice(USER, DisconnectDeviceArgs.withDeviceId(disconnectTargetId))
                    .map(CurrentPlayerState::getDevices)
                    .as(StepVerifier::create)
                    .expectNext(connectedDevices)
                    .verifyComplete();
        }

        @Test
        void shouldThrowExceptionIfDeviceIdIsInvalid() {
            TargetDevices invalidDevices = TargetDevices.single(TargetDevice.of("not_exist"));

            assertThatThrownBy(() -> operations.transferPlayback(USER, SwitchDeviceCommandArgs.noMatter(), empty(), invalidDevices).block())
                    .isInstanceOf(DeviceNotFoundException.class)
                    .hasMessage("Device with provided ID was not found!")
                    .is(reasonCodeEqual("device_not_found"));
        }

        @Test
        void shouldThrowExceptionIfDeviceIdIsMoreThanOne() {
            TargetDevices devices = TargetDevices.multiple(TargetDevice.of("something"), TargetDevice.of("something_else"));
            SwitchDeviceCommandArgs args = ensurePlaybackStarted();

            assertThatThrownBy(() -> operations.transferPlayback(USER, args, empty(), devices).block())
                    .isInstanceOf(MultipleTargetDevicesNotSupportedException.class)
                    .hasMessage("One and only one deviceId should be provided. More than one is not supported now")
                    .is(reasonCodeEqual("multiple_devices_not_supported"));
        }

        @Test
        void shouldThrowExceptionIfTargetDevicesSizeIsZero() {
            TargetDevices devices = TargetDevices.empty();
            SwitchDeviceCommandArgs args = ensurePlaybackStarted();

            assertThatThrownBy(() -> operations.transferPlayback(USER, args, empty(), devices).block())
                    .isInstanceOf(TargetDeviceRequiredException.class)
                    .hasMessage("Target device is required!")
                    .is(reasonCodeEqual("target_device_required"));
        }

        @Test
        void shouldThrowExceptionIfTargetDeactivationDevicesSizeIsMoreThan1() {
            TargetDeactivationDevices deactivationDevices = TargetDeactivationDevicesFaker.create(2).get();
            TargetDevices targetDevices = TargetDevices.single(TargetDevice.of(INACTIVE_DEVICE.getId()));

            assertThatThrownBy(() -> operations.transferPlayback(USER, ensurePlaybackStarted(), deactivationDevices, targetDevices).block())
                    .isInstanceOf(SingleTargetDeactivationDeviceRequiredException.class)
                    .hasMessage("Single deactivation required but was received more or less!");
        }
    }

    @AfterEach
    void tearDown() {
        playerStateRepository.clear().block();
    }

    private static DeviceEntity getActiveDevice(PlayerStateEntity playerState) {
        List<DeviceEntity> activeDeviceEntities = playerState.getDevices().getActiveDevices();
        if ( activeDeviceEntities.size() == 0 ) {
            throw new IllegalStateException("At least one device must be active");
        }
        return activeDeviceEntities.get(0);
    }

    private static DeviceEntity getInactiveDevice(PlayerStateEntity playerState) {
        return playerState.getDevices().stream().filter(not(DeviceEntity::isActive)).findFirst()
                .orElseThrow((() -> new IllegalStateException("At least one device must be inactive")));
    }
}