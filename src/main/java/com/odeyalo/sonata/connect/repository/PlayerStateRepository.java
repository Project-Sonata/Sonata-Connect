package com.odeyalo.sonata.connect.repository;

public interface PlayerStateRepository extends PlayerStatePersistentOperations {
    /**
     * @return the repository type that this repo supports
     */
    RepositoryType getRepositoryType();
}