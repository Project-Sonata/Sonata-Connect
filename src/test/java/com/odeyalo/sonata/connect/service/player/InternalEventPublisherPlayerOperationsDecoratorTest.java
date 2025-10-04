package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.TrackItem;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.messaging.SpyMessageSendingTemplate;
import com.odeyalo.sonata.suite.brokers.events.SonataEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackPausedEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackPlayedEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackResumedEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.test.StepVerifier;
import testing.factory.DefaultPlayerOperationsTestableBuilder;
import testing.faker.PlayableItemFaker.TrackItemFaker;
import testing.faker.TrackItemEntityFaker;

import static com.odeyalo.sonata.connect.service.player.DefaultPlayerOperationsTest.existingPlayerState;
import static org.assertj.core.api.Assertions.assertThat;


class InternalEventPublisherPlayerOperationsDecoratorTest {
    static final User EXISTING_USER = User.of("odeyalooo");
    static final TrackItem TRACK_1 = TrackItemFaker.create().withId("cassie").get();

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ResumeCommandTest {

        private static PlayerStateEntity prepareState() {
            final TrackItemEntity playingItem = TrackItemEntityFaker.create()
                    .withId("cassie")
                    .get();

            return existingPlayerState()
                    .setPlaying(false)
                    .setCurrentlyPlayingItem(playingItem)
                    .setPlayingType(PlayingType.TRACK);
        }

        @Test
        void shouldSendEvent() {
            // given
            final PlayerStateEntity playerState = prepareState();

            final SpyMessageSendingTemplate<String, SonataEvent> template = new SpyMessageSendingTemplate<>();

            final var testable = new InternalEventPublisherPlayerOperationsDecorator(
                    DefaultPlayerOperationsTestableBuilder.testableBuilder()
                            .withState(playerState)
                            .withPlayableItems(TRACK_1)
                            .build(), template
            );

            // when
            testable.playOrResume(EXISTING_USER, PlayCommandContext.resumePlayback())
                    .as(StepVerifier::create)
                    .expectNextCount(1)
                    .verifyComplete();

            // then
            assertThat(template.getRecords()).hasSize(1);
            var record = template.getRecords().get(0);

            assertThat(record.getValue()).isInstanceOf(TrackResumedEvent.class);

            final TrackResumedEvent event = (TrackResumedEvent) record.getValue();

            assertThat(event.getBody().getUserId()).isEqualTo(EXISTING_USER.getId());
            assertThat(event.getBody().getTrackId()).isEqualTo("cassie");
            assertThat(event.getBody().getPosition()).isGreaterThan(0);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PlayCommandTest {

        @Test
        void shouldSendEvent() {
            // given
            final PlayerStateEntity playerState = prepareState();

            final SpyMessageSendingTemplate<String, SonataEvent> template = new SpyMessageSendingTemplate<>();

            final var testable = new InternalEventPublisherPlayerOperationsDecorator(
                    DefaultPlayerOperationsTestableBuilder.testableBuilder()
                            .withState(playerState)
                            .withPlayableItems(TRACK_1)
                            .build(), template
            );

            // when
            testable.playOrResume(EXISTING_USER, PlayCommandContext.of(ContextUri.forTrack("cassie")))
                    .as(StepVerifier::create)
                    .expectNextCount(1)
                    .verifyComplete();

            // then
            assertThat(template.getRecords()).hasSize(1);
            var record = template.getRecords().get(0);

            assertThat(record.getValue()).isInstanceOf(TrackPlayedEvent.class);

            final TrackPlayedEvent event = (TrackPlayedEvent) record.getValue();

            assertThat(event.getBody().getUserId()).isEqualTo(EXISTING_USER.getId());
            assertThat(event.getBody().getTrackId()).isEqualTo("cassie");
            assertThat(event.getBody().getPosition()).isEqualTo(0);
        }

        @NotNull
        private static PlayerStateEntity prepareState() {
            return existingPlayerState()
                    .setPlaying(false);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PauseCommandTest {

        @Test
        void shouldSendEvent() {
            // given
            final PlayerStateEntity playerState = prepareState();

            final SpyMessageSendingTemplate<String, SonataEvent> template = new SpyMessageSendingTemplate<>();

            final DefaultPlayerOperations delegate = DefaultPlayerOperationsTestableBuilder.testableBuilder()
                    .withState(playerState)
                    .withPlayableItems(TRACK_1)
                    .build();

            delegate.playOrResume(EXISTING_USER, PlayCommandContext.of(ContextUri.forTrack("cassie"))).block();

            final var testable = new InternalEventPublisherPlayerOperationsDecorator(
                    delegate, template
            );

            // when
            testable.pause(EXISTING_USER)
                    .as(StepVerifier::create)
                    .expectNextCount(1)
                    .verifyComplete();

            // then
            assertThat(template.getRecords()).hasSize(1);
            var record = template.getRecords().get(0);

            assertThat(record.getValue()).isInstanceOf(TrackPausedEvent.class);

            final TrackPausedEvent event = (TrackPausedEvent) record.getValue();

            assertThat(event.getBody().getUserId()).isEqualTo(EXISTING_USER.getId());
            assertThat(event.getBody().getTrackId()).isEqualTo("cassie");
            assertThat(event.getBody().getPosition()).isGreaterThan(0);
        }

        @Test
        void shouldNotSendAnyEventIfCommandDidntWork() {
            // given
            final PlayerStateEntity playerState = prepareState();

            final SpyMessageSendingTemplate<String, SonataEvent> template = new SpyMessageSendingTemplate<>();

            final var testable = new InternalEventPublisherPlayerOperationsDecorator(
                    DefaultPlayerOperationsTestableBuilder.testableBuilder()
                            .withState(playerState)
                            .build(), template
            );

            // when
            testable.pause(EXISTING_USER)
                    .as(StepVerifier::create)
                    .expectNextCount(1)
                    .verifyComplete();

            // then
            assertThat(template.getRecords()).isEmpty();
        }

        @NotNull
        private static PlayerStateEntity prepareState() {
            return existingPlayerState()
                    .setPlaying(false)
                    .setCurrentlyPlayingItem(null);
        }
    }
}
