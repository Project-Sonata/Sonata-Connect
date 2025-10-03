package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.service.messaging.MessageSendingTemplate;
import com.odeyalo.sonata.suite.brokers.events.SonataEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackResumedEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.payload.TrackPlayedPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Primary
public final class InternalEventPublisherPlayerOperationsDecorator implements BasicPlayerOperations {
    private final BasicPlayerOperations delegate;
    private final MessageSendingTemplate<String, SonataEvent> messageSendingTemplate;
    private final Logger logger = LoggerFactory.getLogger(InternalEventPublisherPlayerOperationsDecorator.class);

    public InternalEventPublisherPlayerOperationsDecorator(@Qualifier("eventPublisherPlayerOperationsDecorator") final BasicPlayerOperations delegate,
                                                           final MessageSendingTemplate<String, SonataEvent> messageSendingTemplate) {
        this.delegate = delegate;
        this.messageSendingTemplate = messageSendingTemplate;
    }

    @Override
    public @NotNull Mono<CurrentPlayerState> currentState(@NotNull final User user) {
        return delegate.currentState(user);
    }

    @Override
    public @NotNull Mono<CurrentlyPlayingPlayerState> currentlyPlayingState(@NotNull final User user) {
        return delegate.currentlyPlayingState(user);
    }

    @Override
    public @NotNull Mono<CurrentPlayerState> changeShuffle(@NotNull final User user, @NotNull final ShuffleMode shuffleMode) {
        return delegate.changeShuffle(user, shuffleMode);
    }

    @Override
    public @NotNull Mono<CurrentPlayerState> playOrResume(@NotNull final User user,
                                                          @NotNull final PlayCommandContext context,
                                                          @Nullable final TargetDevice targetDevice) {


        return delegate.playOrResume(user, context, targetDevice)
                .flatMap(state -> {
                    if ( state.getPlayableItem() == null ) {
                        return Mono.just(state);
                    }

                    Mono<Void> send = sendPlayerPlayCommand(user, context, state);

                    return send.thenReturn(state);
                });
    }

    @NotNull
    private Mono<Void> sendPlayerPlayCommand(@NotNull final User user,
                                             @NotNull final PlayCommandContext context,
                                             @NotNull final CurrentPlayerState state) {

        if ( state.getPlayableItem() == null ) {
            throw new IllegalStateException("To send 'PLAY' or 'RESUME' player command playable item is required");
        }

        final TrackPlayedPayload payload = new TrackPlayedPayload(
                user.getId(),
                state.getPlayableItem().getId(),
                (int) state.getProgressMs()
        );

        if ( context.shouldBeResumed() ) {
            return send(new TrackResumedEvent(payload));
        }

        return Mono.empty();
    }

    @Override
    public @NotNull Mono<CurrentPlayerState> pause(@NotNull final User user) {

        return delegate.pause(user);

//        return delegate.pause(user)
//                .flatMap(state -> {
//                    TrackPausedEvent event = new TrackPausedEvent(
//                            user.getId(),
//                            UUID.randomUUID().toString(),
//                            Instant.now(),
//                            (int) state.getProgressMs()
//                    );
//
//                   return send(event).thenReturn(state);
//                });
//
//        ;
    }

    @NotNull
    private Mono<Void> send(@NotNull final SonataEvent event) {
        logger.info("Send event: [{}, {}] to Kafka cluster ", event.id(), event.getEventType());

        var toSend = Mono.just(
                MessageSendingTemplate.Record.<String, SonataEvent>create(
                        "activity.player", event
                )
        );

        return messageSendingTemplate.send(toSend)
                .then();
    }

    @Override
    public @NotNull Mono<CurrentPlayerState> changeVolume(@NotNull final User user, @NotNull final Volume volume) {
        return delegate.changeVolume(user, volume);
    }

    @Override
    public @NotNull Mono<CurrentPlayerState> seekToPosition(@NotNull final User user, @NotNull final SeekPosition position) {
        return delegate.seekToPosition(user, position);
    }
}
