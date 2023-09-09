package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.exception.DeviceNotFoundException;
import com.odeyalo.sonata.connect.exception.MultipleTargetDevicesNotSupportedException;
import com.odeyalo.sonata.connect.exception.SingleTargetDeactivationDeviceRequiredException;
import com.odeyalo.sonata.connect.exception.TargetDeviceRequiredException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.DevicesModel;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.repository.storage.RepositoryDelegatePlayerStateStorage;
import com.odeyalo.sonata.connect.repository.storage.support.InMemory2PersistablePlayerStateConverter;
import com.odeyalo.sonata.connect.service.player.handler.SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.Device2DeviceModelConverter;
import com.odeyalo.sonata.connect.service.support.mapper.DevicesToDevicesModelConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PersistablePlayerState2CurrentPlayerStateConverter;
import com.odeyalo.sonata.connect.service.support.mapper.PlayableItemEntity2PlayableItemConverter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import testing.asserts.DevicesModelAssert;
import testing.faker.PlayerStateFaker;
import testing.faker.TargetDeactivationDevicesFaker;

import java.util.List;

import static com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs.ensurePlaybackStarted;
import static com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices.empty;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static testing.condition.Conditions.reasonCodeEqual;

class DefaultStorageDeviceOperationsTest {

    PlayerStateStorage storage = new RepositoryDelegatePlayerStateStorage(
            new InMemoryPlayerStateRepository(),
            new InMemory2PersistablePlayerStateConverter()
    );
    PersistablePlayerState2CurrentPlayerStateConverter playerStateConverter =
            new PersistablePlayerState2CurrentPlayerStateConverter(
                    new DevicesToDevicesModelConverter(new Device2DeviceModelConverter()),
                    new PlayableItemEntity2PlayableItemConverter());

    DefaultStorageDeviceOperations operations = new DefaultStorageDeviceOperations(storage,
            new SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate(storage, playerStateConverter));

    User user;
    Device activeDevice;
    Device inactiveDevice;

    @BeforeEach
    void prepare() {
        PlayerState playerState = storage.save(PlayerStateFaker.createWithCustomNumberOfDevices(3).asPersistablePlayerState()).block();
        user = User.of(playerState.getUser().getId());
        activeDevice = getActiveDevice(playerState);
        inactiveDevice = getInactiveDevice(playerState);
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class TransferPlaybackTests {

        @Test
        void transferAndExpectActiveDeviceToBecomeInactive() {
            SwitchDeviceCommandArgs args = ensurePlaybackStarted();
            TargetDevices devices = TargetDevices.single(TargetDevice.of(inactiveDevice.getId()));

            CurrentPlayerState updatedPlayerState = operations.transferPlayback(user, args, empty(), devices).block();

            DevicesModel actualDevices = updatedPlayerState.getDevices();

            DevicesModelAssert.forDevices(actualDevices)
                    .peekById(activeDevice.getId()).inactive();
        }

        @Test
        void transferAndExpectTargetDeviceToBecomeActive() {
            SwitchDeviceCommandArgs args = ensurePlaybackStarted();
            TargetDevices devices = TargetDevices.single(TargetDevice.of(inactiveDevice.getId()));

            CurrentPlayerState updatedPlayerState = operations.transferPlayback(user, args, empty(), devices).block();

            DevicesModel actualDevices = updatedPlayerState.getDevices();

            DevicesModelAssert.forDevices(actualDevices)
                    .peekById(inactiveDevice.getId()).active();
        }


        @Test
        void shouldThrowExceptionIfDeviceIdIsInvalid() {
            TargetDevices invalidDevices = TargetDevices.single(TargetDevice.of("not_exist"));

            assertThatThrownBy(() -> operations.transferPlayback(user, SwitchDeviceCommandArgs.noMatter(), empty(), invalidDevices).block())
                    .isInstanceOf(DeviceNotFoundException.class)
                    .hasMessage("Device with provided ID was not found!")
                    .is(reasonCodeEqual("device_not_found"));
        }

        @Test
        void shouldThrowExceptionIfDeviceIdIsMoreThanOne() {
            TargetDevices devices = TargetDevices.multiple(TargetDevice.of("something"), TargetDevice.of("something_else"));
            SwitchDeviceCommandArgs args = ensurePlaybackStarted();

            assertThatThrownBy(() -> operations.transferPlayback(user, args, empty(), devices).block())
                    .isInstanceOf(MultipleTargetDevicesNotSupportedException.class)
                    .hasMessage("One and only one deviceId should be provided. More than one is not supported now")
                    .is(reasonCodeEqual("multiple_devices_not_supported"));
        }

        @Test
        void shouldThrowExceptionIfTargetDevicesSizeIsZero() {
            TargetDevices devices = TargetDevices.empty();
            SwitchDeviceCommandArgs args = ensurePlaybackStarted();

            assertThatThrownBy(() -> operations.transferPlayback(user, args, empty(), devices).block())
                    .isInstanceOf(TargetDeviceRequiredException.class)
                    .hasMessage("Target device is required!")
                    .is(reasonCodeEqual("target_device_required"));
        }

        @Test
        void shouldThrowExceptionIfTargetDeactivationDevicesSizeIsMoreThan1() {
            TargetDeactivationDevices deactivationDevices = TargetDeactivationDevicesFaker.create(2).get();
            TargetDevices targetDevices = TargetDevices.single(TargetDevice.of(inactiveDevice.getId()));

            assertThatThrownBy(() -> operations.transferPlayback(user, ensurePlaybackStarted(), deactivationDevices, targetDevices).block())
                    .isInstanceOf(SingleTargetDeactivationDeviceRequiredException.class)
                    .hasMessage("Single deactivation required but was received more or less!");
        }
    }

    @AfterEach
    void tearDown() {
        storage.clear().block();
    }

    private static Device getActiveDevice(PlayerState playerState) {
        List<Device> activeDevices = playerState.getDevices().getActiveDevices();
        if (activeDevices.size() == 0) {
            throw new IllegalStateException("At least one device must be active");
        }
        return activeDevices.get(0);
    }

    private static Device getInactiveDevice(PlayerState playerState) {
        return playerState.getDevices().stream().filter(not(Device::isActive)).findFirst()
                .orElseThrow((() -> new IllegalStateException("At least one device must be inactive")));
    }
}