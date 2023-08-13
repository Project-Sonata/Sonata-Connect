package com.odeyalo.sonata.connect.repository;

import com.odeyalo.sonata.connect.entity.InMemoryPlayerState;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryPlayerStateRepository implements PlayerStateRepository<InMemoryPlayerState> {
    private final Map<Long, InMemoryPlayerState> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheByUserId = new ConcurrentHashMap<>();

    @Override
    public Mono<InMemoryPlayerState> save(InMemoryPlayerState entity) {
        if (entity.getId() == null || entity.getId() <= 0) {
            return Mono.error(new IllegalArgumentException("The id is invalid"));
        }
        return Mono.fromRunnable(() -> {
                    cache.put(entity.getId(), entity);
                    cacheByUserId.put(entity.getUser().getId(), entity.getId());
                })
                .thenReturn(entity);
    }

    @Override
    public Mono<InMemoryPlayerState> findById(Long id) {
        return Mono.justOrEmpty(cache.get(id));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.justOrEmpty(cache.remove(id))
                .then();
    }

    @Override
    public Mono<Long> count() {
        return Mono.just(cache.size())
                .map(Long::valueOf);
    }

    @Override
    public RepositoryType getRepositoryType() {
        return RepositoryType.IN_MEMORY;
    }

    @Override
    public Mono<InMemoryPlayerState> findByUserId(String userId) {
        return Mono.justOrEmpty(cacheByUserId.get(userId))
                .map(cache::get);
    }
}
