package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import testing.faker.CurrentPlayerStateFaker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventPublisherDeviceOperationsDecoratorTest {

    @Test
    void shouldSendEventToSynchronizationManagerOnDisconnectDeviceCommand() {
        User user = User.of("123");
        CurrentPlayerState playerState = CurrentPlayerStateFaker.create().withUser(user).get();

        DeviceOperations delegateMock = mock(DeviceOperations.class);
        PlayerSynchronizationManager synchronizationManagerMock = mock(PlayerSynchronizationManager.class);

        when(delegateMock.disconnectDevice(any(), any())).thenReturn(Mono.just(playerState));
        when(synchronizationManagerMock.publishUpdatedState(any(), any())).thenReturn(Mono.empty());

        EventPublisherDeviceOperationsDecorator testable = new EventPublisherDeviceOperationsDecorator(delegateMock, synchronizationManagerMock);

        testable.disconnectDevice(user, DisconnectDeviceArgs.withDeviceId("my_device_id")).block();

        verify(delegateMock, times(1)).disconnectDevice(eq(user), any());
        verify(synchronizationManagerMock, times(1)).publishUpdatedState(eq(user), any());
    }
}