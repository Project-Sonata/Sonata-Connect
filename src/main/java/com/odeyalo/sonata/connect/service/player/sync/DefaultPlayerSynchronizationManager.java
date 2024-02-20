package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Default PlayerSynchronizationManager that just delegate job to RoomEventPublisher
 */
@Service
public class DefaultPlayerSynchronizationManager implements PlayerSynchronizationManager {
    private final RoomHolder roomHolder;

    @Autowired
    public DefaultPlayerSynchronizationManager(RoomHolder roomHolder) {
        this.roomHolder = roomHolder;
    }

    @Override
    public Mono<Void> publishUpdatedState(User user, PlayerEvent event) {
        return roomHolder.getOrCreateRoom(user).flatMap(room -> room.getPublisher().publishEvent(event));
    }

    @Override
    public Flux<PlayerEvent> getEventStream(User user) {
        return roomHolder.getOrCreateRoom(user).flatMapMany(room -> room.getPublisher().getEventStream());
    }
}
