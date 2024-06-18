package com.odeyalo.sonata.connect.service.player.support;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.PlayableItem;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of a {@link PlayableItemLoader} that returns predefined set of {@link PlayableItem}
 */
@Component
public final class PredefinedPlayableItemLoader implements PlayableItemLoader {
    private final Map<ContextUri, PlayableItem> cache;

    public PredefinedPlayableItemLoader() {
        this.cache = new ConcurrentHashMap<>();
    }

    public PredefinedPlayableItemLoader(final List<PlayableItem> items) {
        this.cache = items.stream().collect(
                Collectors.toMap(
                        PlayableItem::getContextUri,
                        Function.identity()
                )
        );
    }

    @Override
    @NotNull
    public Mono<PlayableItem> loadPlayableItem(@NotNull final ContextUri contextUri) {
        return Mono.just(
                cache.get(contextUri)
        );
    }
}
