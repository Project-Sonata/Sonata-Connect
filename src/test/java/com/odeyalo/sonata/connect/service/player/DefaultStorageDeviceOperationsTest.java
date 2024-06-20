package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.config.Converters;
import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.InMemoryPlayerStateRepository;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.handler.TransferPlaybackCommandHandlerDelegate;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.test.StepVerifier;
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayerStateFaker;

import java.util.Objects;

class DefaultStorageDeviceOperationsTest {

    PlayerStateRepository playerStateRepository = new InMemoryPlayerStateRepository();

    PlayerState2CurrentPlayerStateConverter playerStateConverter = new Converters().playerState2CurrentPlayerStateConverter();

    DefaultStorageDeviceOperations operations = new DefaultStorageDeviceOperations(playerStateRepository,
            Mockito.mock(TransferPlaybackCommandHandlerDelegate.class), playerStateConverter,
            new Converters().devicesEntity2DevicesConverter(),
            new DeviceEntity.Factory()
    );

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

    @AfterEach
    void tearDown() {
        playerStateRepository.clear().block();
    }
}