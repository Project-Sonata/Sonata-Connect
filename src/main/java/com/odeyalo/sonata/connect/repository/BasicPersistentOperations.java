package com.odeyalo.sonata.connect.repository;

import reactor.core.publisher.Mono;

/**
 * Represent basic operations that can be done for entity
 * @param <T> - entity type
 * @param <ID> - id of the enity
 */
public interface BasicPersistentOperations<T, ID> {
    /**
     * Save the given entity
     * @param entity - entity to save
     * @throws IllegalStateException if the entity has invalid signature, invalid value, etc
     * @return mono with saved entity
     */
    Mono<T> save(T entity);

    /**
     * Search for the entity by given id
     * @param id - id associated with entity
     * @return - found entity in mono, empty mono if nothing was found
     */
    Mono<T> findById(ID id);

    /**
     * Delete the entity by id, do nothing if the entity with this id does not exist
     * @param id - id associated with entity
     * @return - empty mono in any case
     */
    Mono<Void> deleteById(ID id);

    /**
     * Count all values in this storage
     * @return - number of objects in this storage, never null or negative value
     */
    Mono<Long> count();

    /**
     * Clear the given storage, useful for tests
     * @return - clear the given storage, returns nothing.
     */
    Mono<Void> clear();
}