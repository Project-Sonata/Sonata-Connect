package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.PlayerState;

public interface PlayerStateRepository<T extends PlayerState> extends PlayerStatePersistentOperations<T> {
    /**
     * @return the repository type that this repo supports
     */
    RepositoryType getRepositoryType();
}