package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.sonata.connect.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static reactor.core.publisher.Mono.fromCallable;

/**
 * Base implementation that store the values in Map
 */
@Component
public class InMemoryRoomHolder implements RoomHolder {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(InMemoryRoomHolder.class);

    @Override
    public Mono<Room> getOrCreateRoom(User user) {
        Assert.notNull(user, "User cannot be null!");
        String userId = user.getId();
        return fromCallable(() -> rooms.computeIfAbsent(userId, (key) -> {
            logger.info("User: {} does not contain ROOM. Creating a new one", user.getId());
            return new SinkRoom();
        }));
    }
}
