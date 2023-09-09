package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.RepeatState;

/**
 * Entity to represent the current player state for the specfic user
 */
public interface PlayerState {
    boolean SHUFFLE_ENABLED = true;
    boolean SHUFFLE_DISABLED = false;

    /**
     * @return ID of the state
     */
    Long getId();

    /**
     * @return repeat state for current player
     */
    RepeatState getRepeatState();

    /**
     * @return shuffle state for the player, true - shuffle enabled, false - shuffle disabled
     */
    boolean getShuffleState();

    /**
     * @return Progress of the track, episode, podcast, etc associated with current player
     */
    Long getProgressMs();

    /**
     * @return the current playing type
     */
    PlayingType getCurrentlyPlayingType();

    /**
     *
     * @return item that is playing right now. Null if player state is empty
     */
    PlayableItemEntity getCurrentlyPlayingItem();

    /**
     * @return True if the player is playing something, false otherwise
     */
    boolean isPlaying();

    /**
     * @return Devices associated with current player
     */
    DevicesEntity getDevices();

    /**
     * @return Owner of the player
     */
    UserEntity getUser();
}
