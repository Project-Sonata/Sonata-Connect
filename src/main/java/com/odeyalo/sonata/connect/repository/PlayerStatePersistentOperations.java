package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import reactor.core.publisher.Mono;

public interface PlayerStatePersistentOperations extends BasicPersistentOperations<PlayerStateEntity, Long> {
    /**
     * Search for the PlayerState associated with given user
     * @param userId - user id to use
     * @return - mono with user or empty
     */
    Mono<PlayerStateEntity> findByUserId(String userId);
}