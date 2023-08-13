package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.PlayerState;
import reactor.core.publisher.Mono;

public interface PlayerStatePersistentOperations<T extends PlayerState> extends BasicPersistentOperations<T, Long> {
    /**
     * Search for the PlayerState associated with given user
     * @param userId - user id to use
     * @return - mono with user or empty
     */
    Mono<T> findByUserId(String userId);
}