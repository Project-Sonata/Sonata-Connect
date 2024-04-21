package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayableItemType;
import org.jetbrains.annotations.NotNull;

/**
 * Represent an entity of the item that can be played by a user.
 */
public interface PlayableItemEntity {
    @NotNull
    String getId();

    @NotNull
    PlayableItemType getType();
}
