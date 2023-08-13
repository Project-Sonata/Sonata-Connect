package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import reactor.core.publisher.Mono;

/**
 * Base interface that provide basic methods for player, such current player state, state updating, etc.
 * This interface is also responsible for notification the subscribers(websockets, long pooling, etc.) on any player state update
 */
public interface BasicPlayerOperations {
    boolean SHUFFLE_ENABLED = true;
    boolean SHUFFLE_DISABLED = false;

    /**
     * Return the current state for the user
     *
     * @param user - user that owns the player state
     * @return - mono wrapped with player state, empty mono if user owns nothing
     */
    Mono<CurrentPlayerState> currentState(User user);

    /**
     * Change the shuffle mode to provided in params.
     * If shuffle mode is already same as provided DO NOTHING.
     *
     * @param user        - user that requested the shuffle update and is owner of the state
     * @param shuffleMode - mode to update shuffle state
     * @return mono with updated player state
     */
    Mono<CurrentPlayerState> changeShuffle(User user, boolean shuffleMode);

    /**
     * Alias for  #changeShuffle(User, true) method call
     */
    default Mono<CurrentPlayerState> enableShuffle(User user) {
        return changeShuffle(user, SHUFFLE_ENABLED);
    }

    /**
     * Alias for  #changeShuffle(User, false) method call
     */
    default Mono<CurrentPlayerState> disableShuffle(User user) {
        return changeShuffle(user, SHUFFLE_DISABLED);
    }
}
