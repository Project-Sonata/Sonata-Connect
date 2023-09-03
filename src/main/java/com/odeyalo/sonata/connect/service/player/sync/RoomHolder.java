package com.odeyalo.sonata.connect.service.player.sync;

import com.odeyalo.suite.security.auth.AuthenticatedUser;
import reactor.core.publisher.Mono;

/**
 * A simple holder that holds the room for the AuthenticatedUser. One user has ONLY one room
 */
public interface RoomHolder {
    /**
     * Retrieve or create a new one room for this user
     * @param user - user to get or create room to
     * @return - room wrapped in mono
     */
    Mono<Room> getOrCreateRoom(AuthenticatedUser user);
}
