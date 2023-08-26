package com.odeyalo.sonata.connect.model;

public interface PlayableItem {
    /**
     * Get the id of the item that can be played
     * @return - id of the item, never null
     */
    String getId();

    PlayableItemType getItemType();
}