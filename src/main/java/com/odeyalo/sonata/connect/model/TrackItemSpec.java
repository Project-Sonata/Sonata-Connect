package com.odeyalo.sonata.connect.model;

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


    @Override
    @NotNull
    default PlayableItemType getItemType() {
        return PlayableItemType.TRACK;
    }
}
