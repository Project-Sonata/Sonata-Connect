package com.odeyalo.sonata.connect.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represent a playable item that has information about duration
 *
 * @see PlayableItem
 */
public interface TimedItem {
    /**
     * @return duration of this item
     */
    @NotNull
    PlayableItemDuration getDuration();
}
