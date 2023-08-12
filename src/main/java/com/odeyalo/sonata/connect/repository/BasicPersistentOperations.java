package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.PlayerState;
import reactor.core.publisher.Mono;

public interface BasicPersistentOperations<T, ID> {

    Mono<T> save(T entity);

    Mono<T> findById(ID id);

    Mono<Void> deleteById(ID id);

    Mono<Long> count();
}