package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.common.context.ContextUri;
import org.jetbrains.annotations.NotNull;

public interface PlayableItem {
    /**
     * Get the id of the item that can be played
     * @return - id of the item, never null
     */
    @NotNull
    String getId();

    @NotNull
    PlayableItemType getItemType();

    @NotNull
    ContextUri getContextUri();

    /**
     * @return duration of this item
     */
    @NotNull
    PlayableItemDuration getDuration();
}