package com.odeyalo.sonata.connect.repository.storage;

import com.odeyalo.sonata.connect.repository.PlayerStatePersistentOperations;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import reactor.core.publisher.Mono;

/**
 * PlayerStateStorage is used as middleware between service and repository(or any other data provider).
 * Main goal of this interface and implementations is to decouple the services to specific repository implementations.
 *
 * While it can make sense just to use the different repositories and use one as delegator,
 * from my point of view, it doesn't have sense and make code harder to understand.
 *
 * Implementations can provide load balancing or check the health of the repositories
 * and use different types of storage if one repository does not respond or do whatever implementation want.
 *
 * Default implementation is {@link RepositoryDelegatePlayerStateStorage} that just delegates the job to first repository
 *
 * @see PlayerStateRepository
 * @see PlayerStatePersistentOperations
 */
public interface PlayerStateStorage extends PlayerStatePersistentOperations<PersistablePlayerState> {

    Mono<PersistablePlayerState> save(PersistablePlayerState playerState);

}
