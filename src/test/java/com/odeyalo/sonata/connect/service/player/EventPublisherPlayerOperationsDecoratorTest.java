package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.service.player.sync.DefaultPlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.InMemoryRoomHolder;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testing.factory.DefaultPlayerOperationsTestableBuilder;
import testing.faker.CurrentPlayerStateFaker;
import testing.faker.DeviceEntityFaker;
import testing.faker.PlayableItemFaker.TrackItemFaker;
import testing.faker.PlayerStateFaker;
import testing.stub.NullDeviceOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.odeyalo.sonata.connect.service.player.BasicPlayerOperations.CURRENT_DEVICE;
import static com.odeyalo.sonata.connect.service.player.EventPublisherPlayerOperationsDecoratorTest.TestableBuilder.testableBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EventPublisherPlayerOperationsDecoratorTest {
    public static final User USER = User.of("odeyalo");
    public static final String EXISTING_TRACK_CONTEXT_URI = "sonata:track:miku";
    public static final PlayCommandContext VALID_PLAY_COMMAND_CONTEXT = PlayCommandContext.of(EXISTING_TRACK_CONTEXT_URI);
    public static final TrackItem TRACK_1 = TrackItemFaker.create().withContextUri(EXISTING_TRACK_CONTEXT_URI).get();


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PlayCommandTest {

        @Test
        void shouldSendEventOnPlayCommand() {
            // given
            PlayerStateEntity pausedPlayerState = PlayerStateFaker
                    .forUser(USER)
                    .paused()
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(pausedPlayerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .withPlayableItems(TRACK_1)
                    .build();
            // when
            testable.playOrResume(USER, VALID_PLAY_COMMAND_CONTEXT, CURRENT_DEVICE)
                    .as(StepVerifier::create)
                    // then
                    .assertNext(it -> assertThat(synchronizationManagerMock.getOccurredEvents()).hasSize(1))
                    .verifyComplete();
        }

        @Test
        void shouldUseActiveDeviceIdForDeviceThatChanged() {
            DeviceEntity activeDevice = DeviceEntityFaker.createActiveDevice().get();
            // given
            PlayerStateEntity pausedPlayerState = PlayerStateFaker
                    .forUser(USER)
                    .paused()
                    .device(activeDevice)
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(pausedPlayerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .withPlayableItems(TRACK_1)
                    .build();
            // when
            testable.playOrResume(USER, VALID_PLAY_COMMAND_CONTEXT, CURRENT_DEVICE)
                    .map(it -> synchronizationManagerMock.getOccurredEvents().get(0))
                    .as(StepVerifier::create)
                    // then
                    .assertNext(it -> assertThat(it.getDeviceThatChanged()).isEqualTo(activeDevice.getId()))
                    .verifyComplete();
        }

        @Test
        void shouldSendEventToSynchronizationManagerOnPlayCommandWithPlayingActive() {
            // given
            PlayerStateEntity pausedPlayerState = PlayerStateFaker
                    .forUser(USER)
                    .paused()
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(pausedPlayerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .withPlayableItems(TRACK_1)
                    .build();
            // when
            testable.playOrResume(USER, VALID_PLAY_COMMAND_CONTEXT, CURRENT_DEVICE)
                    .map(it -> synchronizationManagerMock.getOccurredEvents().get(0))
                    .as(StepVerifier::create)
                    // then
                    .assertNext(it -> assertThat(it.getCurrentPlayerState().isPlaying()).isTrue())
                    .verifyComplete();
        }

        @Test
        void shouldInvokeDelegatePlayMethod() {
            CurrentPlayerState playerState = CurrentPlayerStateFaker.create()
                    .progressed()
                    .get();

            BasicPlayerOperations delegateMock = mock(BasicPlayerOperations.class);

            when(delegateMock.playOrResume(eq(USER), eq(VALID_PLAY_COMMAND_CONTEXT), eq(CURRENT_DEVICE))).thenReturn(Mono.just(playerState));

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withDelegate(delegateMock)
                    .build();

            testable.playOrResume(USER, VALID_PLAY_COMMAND_CONTEXT, CURRENT_DEVICE)
                    .as(StepVerifier::create)
                    .assertNext(it -> verify(delegateMock, times(1))
                            .playOrResume(eq(USER), eq(VALID_PLAY_COMMAND_CONTEXT), eq(CURRENT_DEVICE)))
                    .verifyComplete();
        }
    }

    @Nested
    class PauseCommandTest {

        @Test
        void shouldSendEventToSynchronizationManagerOnPauseCommand() {
            // given
            PlayerStateEntity pausedPlayerState = PlayerStateFaker
                    .forUser(USER)
                    .paused()
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(pausedPlayerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .build();
            // when
            testable.pause(USER)
                    .as(StepVerifier::create)
                    // then
                    .assertNext(it -> assertThat(synchronizationManagerMock.getOccurredEvents()).hasSize(1))
                    .verifyComplete();
        }

        @Test
        void shouldUseActiveDeviceIdForDeviceThatChanged() {
            DeviceEntity activeDevice = DeviceEntityFaker.createActiveDevice().get();
            // given
            PlayerStateEntity pausedPlayerState = PlayerStateFaker
                    .forUser(USER)
                    .paused()
                    .device(activeDevice)
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(pausedPlayerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .build();
            // when
            testable.pause(USER)
                    .map(it -> synchronizationManagerMock.getOccurredEvents().get(0))
                    .as(StepVerifier::create)
                    // then
                    .assertNext(it -> assertThat(it.getDeviceThatChanged()).isEqualTo(activeDevice.getId()))
                    .verifyComplete();
        }

        @Test
        void shouldNotSendAnyEventIfActiveDeviceIsNull() {
            DeviceEntity activeDevice = DeviceEntityFaker.createInactiveDevice().get();
            // given
            PlayerStateEntity pausedPlayerState = PlayerStateFaker
                    .forUser(USER)
                    .paused()
                    .device(activeDevice)
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(pausedPlayerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .build();
            // when
            assertThatThrownBy(() -> testable.pause(USER).block()).isInstanceOf(NoActiveDeviceException.class);
            assertThat(synchronizationManagerMock.getOccurredEvents()).isEmpty();
        }

        @Test
        void shouldSendEventToSynchronizationManagerOnPauseCommandWithPausedField() {
            // given
            PlayerStateEntity pausedPlayerState = PlayerStateFaker
                    .forUser(USER)
                    .paused()
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(pausedPlayerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .build();
            // when
            testable.pause(USER)
                    .map(it -> synchronizationManagerMock.getOccurredEvents().get(0))
                    .as(StepVerifier::create)
                    // then
                    .assertNext(it -> assertThat(it.getCurrentPlayerState().isPlaying()).isFalse())
                    .verifyComplete();
        }

        @Test
        void shouldInvokeDelegatePause() {
            CurrentPlayerState pausedPlayerState = CurrentPlayerStateFaker.create()
                    .paused()
                    .get();

            BasicPlayerOperations delegateMock = mock(BasicPlayerOperations.class);

            when(delegateMock.pause(any())).thenReturn(Mono.just(pausedPlayerState));

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withDelegate(delegateMock)
                    .build();

            testable.pause(USER)
                    .as(StepVerifier::create)
                    .assertNext(it -> verify(delegateMock, times(1)).pause(eq(USER)))
                    .verifyComplete();
        }
    }

    @Nested
    class ChangeVolumeCommandTest  {

        @Test
        void shouldSendEventOnVolumeChange() {
            PlayerStateEntity playerState = PlayerStateFaker
                    .forUser(USER)
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(playerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .build();

            testable.changeVolume(USER, Volume.fromInt(20)).block();

            assertThat(synchronizationManagerMock.getOccurredEvents())
                    .hasSize(1)
                    .first().matches(it -> it.getCurrentPlayerState().getVolume().asInt() == 20);
        }

        @Test
        void shouldUseActiveDeviceId() {
            DeviceEntity activeDevice = DeviceEntityFaker.createActiveDevice()
                    .setDeviceId("miku")
                    .get();
            // given
            PlayerStateEntity playerState = PlayerStateFaker
                    .forUser(USER)
                    .device(activeDevice)
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(playerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .build();
            // when
            testable.pause(USER)
                    .map(it -> synchronizationManagerMock.getOccurredEvents().get(0))
                    .as(StepVerifier::create)
                    // then
                    .assertNext(it -> assertThat(it.getDeviceThatChanged()).isEqualTo("miku"))
                    .verifyComplete();
        }

        @Test
        void shouldNotSendEventIfErrorOccurred() {
            // given
            PlayerStateEntity playerState = PlayerStateFaker
                    .forUser(USER)
                    .devicesEntity(DevicesEntity.empty())
                    .get();

            EventCollectorPlayerSynchronizationManager synchronizationManagerMock = new EventCollectorPlayerSynchronizationManager();

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withPlayerState(playerState)
                    .withSynchronizationManager(synchronizationManagerMock)
                    .build();

            // when, then
            assertThatThrownBy(() -> testable.changeVolume(USER, Volume.fromInt(10)).block()).isInstanceOf(NoActiveDeviceException.class);
            assertThat(synchronizationManagerMock.getOccurredEvents()).isEmpty();
        }

        @Test
        void shouldInvokeDelegateChangeVolumeMethod() {
            CurrentPlayerState playerState = CurrentPlayerStateFaker.create().withUser(USER).get();

            BasicPlayerOperations delegateMock = mock(BasicPlayerOperations.class);

            when(delegateMock.changeVolume(USER, Volume.fromInt(10))).thenReturn(Mono.just(playerState));

            EventPublisherPlayerOperationsDecorator testable = testableBuilder()
                    .withDelegate(delegateMock)
                    .build();

            testable.changeVolume(USER, Volume.fromInt(10)).block();

            verify(delegateMock, times(1)).changeVolume(eq(USER), eq(Volume.fromInt(10)));
        }
    }

    static class TestableBuilder {
        private final DefaultPlayerOperationsTestableBuilder delegateBuilder = DefaultPlayerOperationsTestableBuilder.testableBuilder();
        private BasicPlayerOperations delegate;
        private PlayerSynchronizationManager synchronizationManager = new DefaultPlayerSynchronizationManager(new InMemoryRoomHolder());
        private final DeviceOperations deviceOperations = new NullDeviceOperations();

        public static TestableBuilder testableBuilder() {
            return new TestableBuilder();
        }

        public TestableBuilder withDelegate(BasicPlayerOperations delegate) {
            this.delegate = delegate;
            return this;
        }

        public TestableBuilder withSynchronizationManager(PlayerSynchronizationManager synchronizationManager) {
            this.synchronizationManager = synchronizationManager;
            return this;
        }

        public EventPublisherPlayerOperationsDecorator build() {
            delegate = delegate == null ? delegateBuilder.build() : delegate;
            return new EventPublisherPlayerOperationsDecorator(delegate, synchronizationManager, deviceOperations);
        }

        public TestableBuilder withPlayerState(PlayerStateEntity playerState) {
            delegateBuilder.withState(playerState);
            return this;
        }

        public TestableBuilder withPlayableItems(final PlayableItem... items) {
            delegateBuilder.withPlayableItems(items);
            return this;
        }
    }

    private static class EventCollectorPlayerSynchronizationManager implements PlayerSynchronizationManager {
        private final DefaultPlayerSynchronizationManager delegate = new DefaultPlayerSynchronizationManager(
                new InMemoryRoomHolder()
        );
        private final List<PlayerEvent> occurredEvents = Collections.synchronizedList(new ArrayList<>());

        @Override
        public Mono<Void> publishUpdatedState(User user, PlayerEvent updatedState) {
            return delegate.publishUpdatedState(user, updatedState)
                    .then(Mono.fromRunnable(() -> occurredEvents.add(updatedState)));
        }

        @Override
        public Flux<PlayerEvent> getEventStream(User user) {
            return delegate.getEventStream(user);
        }

        public List<PlayerEvent> getOccurredEvents() {
            return occurredEvents;
        }
    }
}