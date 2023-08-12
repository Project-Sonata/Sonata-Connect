package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.PlayerState;
import org.springframework.data.repository.core.RepositoryMetadata;

public interface PlayerStateRepository<T extends PlayerState> extends PlayerStatePersistentOperations<T> {

    RepositoryType getRepositoryType();
}
