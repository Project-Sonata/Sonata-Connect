package com.odeyalo.sonata.connect.repository.storage;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.repository.storage.support.PersistableEntityConverter;
import reactor.core.publisher.Mono;

public class RepositoryDelegatePlayerStateStorage implements PlayerStateStorage {
    private final PlayerStateRepository<PlayerState> delegate;
    private final PersistableEntityConverter<PlayerState, PersistablePlayerState> converter;

    public RepositoryDelegatePlayerStateStorage(PlayerStateRepository<? extends PlayerState> delegate,
                                                PersistableEntityConverter<? extends PlayerState, PersistablePlayerState> converter) {
        this.delegate = (PlayerStateRepository<PlayerState>) delegate;
        this.converter = (PersistableEntityConverter<PlayerState, PersistablePlayerState>) converter;
    }

    @Override
    public Mono<PersistablePlayerState> findById(Long id) {
        return delegate.findById(id)
                .map(this::toPersistableEntity);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return delegate.deleteById(id);
    }

    @Override
    public Mono<Long> count() {
        return delegate.count();
    }

    @Override
    public Mono<Void> clear() {
        return delegate.clear();
    }

    @Override
    public Mono<PersistablePlayerState> save(PersistablePlayerState playerState) {
        return Mono.just(playerState)
                .map(state -> converter.convertFrom(playerState))
                .flatMap(delegate::save)
                .map(this::toPersistableEntity);
    }

    private PersistablePlayerState toPersistableEntity(PlayerState type) {
        return converter.convertTo(type);
    }

    @Override
    public Mono<PersistablePlayerState> findByUserId(String userId) {
        return delegate.findByUserId(userId)
                .map(converter::convertTo);
    }
}
