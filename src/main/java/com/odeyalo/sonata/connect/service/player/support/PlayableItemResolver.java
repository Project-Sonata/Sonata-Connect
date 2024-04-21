package com.odeyalo.sonata.connect.service.player.support;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import reactor.core.publisher.Mono;
/**
 * The PlayableItemResolver interface defines a contract for resolving a PlayableItem based on a given ContextUri.
 */
public interface PlayableItemResolver {

    /**
     * Resolves a PlayableItem based on the provided ContextUri, PlayCommandContext, and PersistablePlayerState.
     *
     * @param contextUri  Already parsed contextUri that provide metadata about context-uri string
     * @param playContext The PlayCommandContext containing information about the play request.
     * @param currentState The current state of the player that can be used for resolving the item.
     * @return A Mono representing the resolved PlayableItem, or an empty Mono if no item could be resolved.
     */
    Mono<PlayableItem> resolveItem(ContextUri contextUri, PlayCommandContext playContext, PlayerStateEntity currentState);
}
