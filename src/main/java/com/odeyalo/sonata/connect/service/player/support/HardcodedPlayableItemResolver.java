package com.odeyalo.sonata.connect.service.player.support;

import com.odeyalo.sonata.common.context.ContextEntityType;
import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.TrackItem;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import jakarta.ws.rs.NotSupportedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.odeyalo.sonata.common.context.ContextEntityType.TRACK;

/**
 * Implementation of a PlayableItemResolver that is hardcoded and not designed to be easily scalable
 * It can be used in tests to reduce object creation.
 */
@Component
public class HardcodedPlayableItemResolver implements PlayableItemResolver {

    @Override
    public Mono<PlayableItem> resolveItem(ContextUri contextUri, PlayCommandContext playContext, PlayerStateEntity currentState) {
        ContextEntityType type = contextUri.getType();
        if (type != TRACK) {
            return Mono.error(new NotSupportedException("Only track is supported"));
        }
        return Mono.just(TrackItem.of(contextUri.getEntityId()));
    }
}
