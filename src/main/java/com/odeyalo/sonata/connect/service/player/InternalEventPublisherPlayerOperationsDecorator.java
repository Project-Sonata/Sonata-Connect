package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.*;
import com.odeyalo.sonata.suite.brokers.events.SonataEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.TrackPlayedEvent;
import com.odeyalo.sonata.suite.brokers.events.activity.player.payload.TrackPlayedPayload;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
@Primary
public final class InternalEventPublisherPlayerOperationsDecorator implements BasicPlayerOperations {
    private final BasicPlayerOperations delegate;
    private final KafkaSender<String, SonataEvent> kafkaSender;
    private final Logger logger = LoggerFactory.getLogger(InternalEventPublisherPlayerOperationsDecorator.class);

    public InternalEventPublisherPlayerOperationsDecorator(@Qualifier("eventPublisherPlayerOperationsDecorator") final BasicPlayerOperations delegate,
                                                           final KafkaSender<String, SonataEvent> kafkaSender) {
        this.delegate = delegate;
        this.kafkaSender = kafkaSender;
    }
//  TODO: finish integration with Kafka
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

                    System.out.println(state.getProgressMs());

                    final TrackPlayedEvent event = new TrackPlayedEvent(
                            new TrackPlayedPayload(
                                    user.getId(),
                                    state.getPlayableItem().getId(),
                                    (int) state.getProgressMs()
                            )
                    );

                    return send(event).thenReturn(state);
                });
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
                SenderRecord.create(
                        new ProducerRecord<String, SonataEvent>("activity.player", event),
                        "correlationId"+event.id()
                )
        );

        return kafkaSender.send(toSend)
                .doOnNext(res -> logger.info("Sent successfully: {}", event.id()))
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
