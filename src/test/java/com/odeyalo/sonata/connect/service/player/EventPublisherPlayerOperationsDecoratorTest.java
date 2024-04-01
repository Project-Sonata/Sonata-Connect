package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.DefaultPlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.InMemoryRoomHolder;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import testing.faker.CurrentPlayerStateFaker;
import testing.stub.NullDeviceOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EventPublisherPlayerOperationsDecoratorTest {

    public static final User USER = User.of("odeyalo");

    @Test
    void shouldSendEventToSynchronizationManagerOnPauseCommand() {
        CurrentPlayerState pausedPlayerState = CurrentPlayerStateFaker.create()
                .paused()
                .get();

        EventPublisherPlayerOperationsDecorator delegateMock = mock(EventPublisherPlayerOperationsDecorator.class);
        PlayerSynchronizationManager synchronizationManagerMock = new DefaultPlayerSynchronizationManager(
                new InMemoryRoomHolder()
        );

        List<PlayerEvent> events = Collections.synchronizedList(new ArrayList<>());

        synchronizationManagerMock.getEventStream(USER)
                .doOnNext(events::add)
                .subscribe();

        when(delegateMock.pause(any())).thenReturn(Mono.just(pausedPlayerState));

        EventPublisherPlayerOperationsDecorator testable =
                new EventPublisherPlayerOperationsDecorator(delegateMock,
                        synchronizationManagerMock,
                        new NullDeviceOperations()
                );

        testable.pause(USER).block();

        assertThat(events).hasSize(1);
    }

    @Test
    void shouldInvokeDelegatePause() {
        CurrentPlayerState pausedPlayerState = CurrentPlayerStateFaker.create()
                .paused()
                .get();

        EventPublisherPlayerOperationsDecorator delegateMock = mock(EventPublisherPlayerOperationsDecorator.class);
        PlayerSynchronizationManager synchronizationManagerMock = new DefaultPlayerSynchronizationManager(
                new InMemoryRoomHolder()
        );

        when(delegateMock.pause(any())).thenReturn(Mono.just(pausedPlayerState));

        EventPublisherPlayerOperationsDecorator testable =
                new EventPublisherPlayerOperationsDecorator(delegateMock,
                        synchronizationManagerMock,
                        new NullDeviceOperations()
                );

        testable.pause(USER).block();

        verify(delegateMock, times(1)).pause(eq(USER));
    }


}