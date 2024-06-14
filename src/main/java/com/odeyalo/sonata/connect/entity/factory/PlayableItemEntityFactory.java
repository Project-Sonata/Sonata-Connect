package com.odeyalo.sonata.connect.entity.factory;

import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.model.PlayableItem;
import org.jetbrains.annotations.NotNull;

/**
 * A factory to create a {@link PlayableItemEntity} based on other classes
 */
public interface PlayableItemEntityFactory {

    /**
     * Create a {@link PlayableItemEntity} from given {@link PlayableItem}
     *
     * @param item - item to create {@link PlayableItemEntity} from
     * @return - created {@link PlayableItemEntity}. Never null
     * @throws UnsupportedOperationException if a given fuctory does not have support for the given {@link PlayableItem}
     */
    @NotNull
    PlayableItemEntity create(@NotNull PlayableItem item);
}