package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.config.Converters;
import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.entity.factory.DefaultPlayerStateEntityFactory;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import testing.faker.DeviceFaker;

import java.util.Objects;

import static com.odeyalo.sonata.connect.service.player.DefaultDeviceOperationsTest.TestableBuilder.testableBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultDeviceOperationsTest {

    final User USER = User.of("miku");

    @Test
    void shouldConnectDeviceAndMakeItIdleIfOtherDevicesAreAlreadyConnected() {
        final DefaultDeviceOperations testable = testableBuilder()
                .createEmptyPlayerStateFor(USER)
                .connectDevices(
                        DeviceFaker.create().get(),
                        DeviceFaker.create().get()
                )
                .build();

        final Device connectionTarget = Device.builder()
                .deviceId("123")
                .deviceName("miku")
                .deviceType(DeviceType.COMPUTER)
                .status(DeviceSpec.DeviceStatus.IDLE)
                .volume(Volume.from(20))
                .build();

        final CurrentPlayerState playerState = testable.connectDevice(USER, connectionTarget).block();

        assertThat(playerState).isNotNull();

        assertThat(playerState.getDevices())
                .hasSize(3)
                .filteredOn(device -> Objects.equals(device.getDeviceName(), "miku"))
                .first()
                .satisfies(device -> {
                    assertThat(device.getId()).isEqualTo("123");
                    assertThat(device.getType()).isEqualTo(DeviceType.COMPUTER);
                    assertThat(device.getVolume()).isEqualTo(Volume.from(20));
                    assertThat(device.isIdle()).isTrue();
                });
    }

    @Test
    void shouldConnectDeviceAndMakeItActiveIfNoOtherDevicesIsConnected() {
        final DefaultDeviceOperations testable = testableBuilder()
                .createEmptyPlayerStateFor(USER)
                .build();

        final Device connectionTarget = Device.builder()
                .deviceId("123")
                .deviceName("miku")
                .deviceType(DeviceType.COMPUTER)
                .status(DeviceSpec.DeviceStatus.IDLE)
                .volume(Volume.from(20))
                .build();

        final CurrentPlayerState playerState = testable.connectDevice(USER, connectionTarget).block();

        assertThat(playerState).isNotNull();

        assertThat(playerState.getDevices())
                .hasSize(1)
                .first()
                .satisfies(device -> {
                    assertThat(device.getId()).isEqualTo("123");
                    assertThat(device.getName()).isEqualTo("miku");
                    assertThat(device.getType()).isEqualTo(DeviceType.COMPUTER);
                    assertThat(device.getVolume()).isEqualTo(Volume.from(20));
                    assertThat(device.isActive()).isTrue();
                });
    }

    @Test
    void shouldDisconnectDeviceByItsIdIfDeviceIsConnected() {
        final DefaultDeviceOperations testable = testableBuilder()
                .createEmptyPlayerStateFor(USER)
                .connectDevices(
                        DeviceFaker.create().get().withActiveStatus(),
                        DeviceFaker.create().get().withDeviceId("inactive").withIdleStatus()
                )
                .build();

        final CurrentPlayerState playerState = testable.disconnectDevice(USER, DisconnectDeviceArgs.withDeviceId("inactive")).block();

        assertThat(playerState).isNotNull();

        assertThat(playerState.getDevices())
                .extracting(Device::getDeviceId)
                .doesNotContain("inactive");
    }

    @Test
    void shouldNotChangeStateIfDeviceNotExist() {
        final Devices connectedDevices = Devices.of(
                DeviceFaker.create().get().withActiveStatus(),
                DeviceFaker.create().get().withIdleStatus()
        );

        final DefaultDeviceOperations testable = testableBuilder()
                .createEmptyPlayerStateFor(USER)
                .connectDevices(connectedDevices)
                .build();

        final CurrentPlayerState playerState = testable.disconnectDevice(USER, DisconnectDeviceArgs.withDeviceId("not_existing")).block();

        assertThat(playerState).isNotNull();
        assertThat(playerState.getDevices()).containsAll(connectedDevices);
    }


    static final class TestableBuilder {
        private final PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

        private final PlayerState2CurrentPlayerStateConverter playerStateConverter = new Converters().playerState2CurrentPlayerStateConverter();
        private final TransferPlaybackCommandHandlerDelegate transferPlaybackCommandHandlerDelegate = Mockito.mock(TransferPlaybackCommandHandlerDelegate.class);

        @Nullable
        private CurrentPlayerState currentPlayerState;

        public static TestableBuilder testableBuilder() {
            return new TestableBuilder();
        }

        public TestableBuilder createEmptyPlayerStateFor(final User user) {
            currentPlayerState = CurrentPlayerState.emptyFor(user);
            return this;
        }

        public TestableBuilder connectDevices(final Device... devices) {
            return connectDevices(Devices.of(devices));
        }

        public TestableBuilder connectDevices(final Devices devices) {
            if ( currentPlayerState == null ) {
                throw new IllegalStateException("Devices can't be connected if no player state exist. Use createEmptyPlayerStateFor(User) method and then call it");
            }

            for (final Device device : devices) {
                currentPlayerState = currentPlayerState.connectDevice(device);
            }

            return this;
        }

        @NotNull
        public DefaultDeviceOperations build() {
            final PlayerStateService playerStateService = new PlayerStateService(
                    playerStateRepository, playerStateConverter, new DefaultPlayerStateEntityFactory(new DeviceEntity.Factory(), new TrackItemEntity.Factory())
            );

            if ( currentPlayerState != null ) {
                playerStateService.save(currentPlayerState).block();
            }

            return new DefaultDeviceOperations(
                    playerStateService, transferPlaybackCommandHandlerDelegate
            );
        }
    }
}