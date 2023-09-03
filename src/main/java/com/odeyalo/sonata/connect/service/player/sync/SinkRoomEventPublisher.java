package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Publish events to multicast sink
 */
public class SinkRoomEventPublisher implements RoomEventPublisher {
    private final Sinks.Many<PlayerEvent> publisher = Sinks.many().multicast().directBestEffort();

    @Override
    public Mono<Void> publishEvent(PlayerEvent event) {
        return Mono.just(publisher.tryEmitNext(event)).then();
    }

    @Override
    public Flux<PlayerEvent> getEventStream() {
        return publisher.asFlux();
    }
}