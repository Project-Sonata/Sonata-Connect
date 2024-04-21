package com.odeyalo.sonata.connect.model;

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
}