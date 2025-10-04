package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.connect.service.messaging.MessageSendingTemplate;
import com.odeyalo.sonata.suite.brokers.events.SonataEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackPausedEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackPlayedEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackResumedEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackSeekEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.payload.TrackPausedPayload;
import com.odeyalo.sonata.suite.brokers.events.activity.player.payload.TrackPlayedPayload;
import com.odeyalo.sonata.suite.brokers.events.activity.player.payload.TrackSeekPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Primary
public final class InternalEventPublisherPlayerOperationsDecorator implements BasicPlayerOperations {
    private final BasicPlayerOperations delegate;
    private final MessageSendingTemplate<String, SonataEvent> messageSendingTemplate;

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
    @NotNull
    public Mono<CurrentlyPlayingPlayerState> currentlyPlayingState(@NotNull final User user) {
        return delegate.currentlyPlayingState(user);
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> changeShuffle(@NotNull final User user, @NotNull final ShuffleMode shuffleMode) {
        return delegate.changeShuffle(user, shuffleMode);
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> playOrResume(@NotNull final User user,
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

    @Override
    @NotNull
    public Mono<CurrentPlayerState> pause(@NotNull final User user) {

        return delegate.pause(user)
                .flatMap(state -> sendPlaybackPausedEvent(user, state));
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> changeVolume(@NotNull final User user,
                                                 @NotNull final Volume volume) {
        return delegate.changeVolume(user, volume);
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> seekToPosition(@NotNull final User user,
                                                   @NotNull final SeekPosition position) {
        return delegate.currentState(user)
                .flatMap(oldState -> delegate.seekToPosition(user, position)
                         .flatMap(newState -> sendPlayerSeekEvent(user, oldState, newState)));
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
                context.shouldBeResumed() ? (int) state.getProgressMs() : 0
        );

        return context.shouldBeResumed() ?
                send(new TrackResumedEvent(payload)) :
                send(new TrackPlayedEvent(payload));
    }

    @NotNull
    private Mono<CurrentPlayerState> sendPlaybackPausedEvent(@NotNull final User user,
                                                             @NotNull final CurrentPlayerState state) {

        if ( state.getPlayableItem() == null ) {
            return Mono.just(state);
        }

        final TrackPausedEvent event = new TrackPausedEvent(
                new TrackPausedPayload(
                        user.getId(),
                        state.getPlayableItem().getId(),
                        (int) state.getProgressMs()

                )
        );

        return send(event).thenReturn(state);
    }

    @NotNull
    private Mono<CurrentPlayerState> sendPlayerSeekEvent(@NotNull final User user,
                                                         @NotNull final CurrentPlayerState oldState,
                                                         @NotNull final CurrentPlayerState newState) {

        if (newState.getPlayableItem() == null) {
            return Mono.just(newState);
        }

        final TrackSeekEvent event = new TrackSeekEvent(
                new TrackSeekPayload(
                        user.getId(),
                        newState.getPlayableItem().getId(),
                        (int) oldState.getProgressMs(),
                        (int) newState.getProgressMs()
                )
        );

        return send(event).thenReturn(newState);
    }

    @NotNull
    private Mono<Void> send(@NotNull final SonataEvent event) {
        var toSend = Mono.just(
                MessageSendingTemplate.Record.<String, SonataEvent>create(
                        "activity.player", event
                )
        );

        return messageSendingTemplate.send(toSend)
                .then();
    }
}
