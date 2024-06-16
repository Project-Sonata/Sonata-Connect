package com.odeyalo.sonata.connect.service.player.support;

import com.odeyalo.sonata.common.context.ContextEntityType;
import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayableItemDuration;
import com.odeyalo.sonata.connect.model.TrackItem;
import jakarta.ws.rs.NotSupportedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.odeyalo.sonata.common.context.ContextEntityType.TRACK;

/**
 * Implementation of a PlayableItemResolver that is hardcoded and not designed to be easily scalable
 * It can be used in tests to reduce object creation.
 */
@Component
public class HardcodedPlayableItemLoader implements PlayableItemLoader {

    @Override
    @NotNull
    public Mono<PlayableItem> resolveItem(@NotNull final ContextUri contextUri) {
        ContextEntityType type = contextUri.getType();
        if (type != TRACK) {
            return Mono.error(new NotSupportedException("Only track is supported"));
        }
        return Mono.just(TrackItem.of(contextUri.getEntityId(),
                "mock",
                PlayableItemDuration.ofSeconds(100),
                ContextUri.forTrack("mock"))
        );
    }
}
