package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Event publisher that used to publish events in specific room
 */
public interface RoomEventPublisher {

    Mono<Void> publishEvent(PlayerEvent event);

    Flux<PlayerEvent> getEventStream();
}
