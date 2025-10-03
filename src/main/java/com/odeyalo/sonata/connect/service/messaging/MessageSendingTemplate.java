package com.odeyalo.sonata.connect.service.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface MessageSendingTemplate<K, V> {

    @NotNull
    Mono<Void> send(@NotNull Publisher<Record<K, V>> events);

    @Value
    @AllArgsConstructor
    @Builder
    class Record<K, V> {
        @NotNull
        String topic;
        @Nullable
        K key;
        @NotNull
        V value;

        @NotNull
        public static <K, V> Record<K, V> create(@NotNull String topic,
                                                 @NotNull V value) {
            return new Record<>(topic, null, value);
        }

        @NotNull
        public static <K, V> Record<K, V> create(@NotNull String topic,
                                                 @NotNull K key,
                                                 @NotNull V value) {
            return new Record<>(topic, key, value);
        }
    }
}
