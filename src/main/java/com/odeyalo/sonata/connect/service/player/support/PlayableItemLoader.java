package com.odeyalo.sonata.connect.service.player.support;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.PlayableItem;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Defines a contract for loading a {@link PlayableItem} based on a given {@link ContextUri}.
 */
public interface PlayableItemLoader {

    /**
     * Loads a {@link PlayableItem} based on the provided {@link ContextUri}
     *
     * @param contextUri   Already parsed contextUri that provide metadata about context-uri string
     * @return A {@link Mono} representing the resolved {@link PlayableItem}, or an empty {@link Mono} if no item could be load.
     */
    @NotNull
    Mono<PlayableItem> loadPlayableItem(@NotNull ContextUri contextUri);
}
