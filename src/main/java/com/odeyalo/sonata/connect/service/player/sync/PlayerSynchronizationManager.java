package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main interface to synchronize player instance between devices(or clients that can subscribe to events)
 * PlayerSynchronizationManager synchronize player instance for every user by making group of devices and synchronize them all at once
 */
public interface PlayerSynchronizationManager {
    /**
     * Notify about updated state devices ( or other clients )
     * @param updatedState - new state to notify the devices
     * @return - void mono
     */
    Mono<Void> publishUpdatedState(User user, PlayerEvent updatedState);

    /**
     * Provide access to subscribe to the events, all events will be published to this stream,
     * stream can be used to subscribe from WebSocket stream, Text-event-stream, etc.
     * @param user - user to get event stream
     * @return - unbound hot-flux that provide real-time updates pushed from {@link #publishUpdatedState(User, PlayerEvent)}
     */
    Flux<PlayerEvent> getEventStream(User user);
}
