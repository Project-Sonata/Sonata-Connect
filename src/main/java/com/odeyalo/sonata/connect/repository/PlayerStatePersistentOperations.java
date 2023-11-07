package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.PlayerState;
import reactor.core.publisher.Mono;

public interface PlayerStatePersistentOperations extends BasicPersistentOperations<PlayerState, Long> {
    /**
     * Search for the PlayerState associated with given user
     * @param userId - user id to use
     * @return - mono with user or empty
     */
    Mono<PlayerState> findByUserId(String userId);
}