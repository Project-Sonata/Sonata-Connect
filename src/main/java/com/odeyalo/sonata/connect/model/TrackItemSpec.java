package com.odeyalo.sonata.connect.model;

import com.odeyalo.sonata.common.context.ContextUri;
import org.jetbrains.annotations.NotNull;

/**
 * Represent a Track item that can be played
 */
public interface TrackItemSpec extends PlayableItem {
    /**
     * @return name of the track
     */
    @NotNull
    String getName();

    @NotNull
    PlayableItemDuration getDuration();

    @NotNull
    ContextUri getContextUri();

    @Override
    @NotNull
    default PlayableItemType getItemType() {
        return PlayableItemType.TRACK;
    }
}
