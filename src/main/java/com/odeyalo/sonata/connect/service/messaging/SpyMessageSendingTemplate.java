package com.odeyalo.sonata.connect.service.messaging;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public final class SpyMessageSendingTemplate<K, V> implements MessageSendingTemplate<K, V> {
    private final List<Record<K, V>> records = new ArrayList<>();


    @Override
    @NotNull
    public Mono<Void> send(@NotNull final Publisher<Record<K, V>> events) {
        return Flux.from(events)
                .doOnNext(records::add)
                .then();
    }

    @NotNull
    public List<Record<K, V>> getRecords() {
        return records;
    }
}
