package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * Base interface that provide basic methods for player, such current player state, state updating, etc.
 */
public interface BasicPlayerOperations {
    TargetDevice CURRENT_DEVICE = null;

    /**
     * Return the current state for the user
     * The method should create new state for the user if current is not created yet.
     *
     * @param user - user that owns the player state
     * @return - mono wrapped with player state, never returns empty mono.
     */
    @NotNull
    Mono<CurrentPlayerState> currentState(@NotNull User user);

    /**
     * Return the currently playing state, if nothing is playing right now empty mono should be returned.
     *
     * @param user - user to get the current player state
     * @return - currently playing state or  empty mono
     */
    @NotNull
    Mono<CurrentlyPlayingPlayerState> currentlyPlayingState(@NotNull User user);

    /**
     * Create or return the player state for the user.
     * Associate it with user and add ability to access it in the future
     *
     * @param user - user to create state to
     * @return - mono with created player state or state that already present
     */
    @NotNull
    default Mono<CurrentPlayerState> createState(@NotNull User user) {
        return currentState(user);
    }

    /**
     * Change the shuffle mode to provided in params.
     * If shuffle mode is already same as provided DO NOTHING.
     *
     * @param user        - user that requested the shuffle update and is owner of the state
     * @param shuffleMode - mode to update shuffle state
     * @return mono with updated player state
     */
    @NotNull
    Mono<CurrentPlayerState> changeShuffle(@NotNull User user, @NotNull ShuffleMode shuffleMode);

    /**
     * Start or resume the track.
     * If track not specified then currently playing track starts to play
     *
     * @param user         - owner of the player state
     * @param context      - context to start playing, if null then current context should be used
     * @param targetDevice - device to start playing to, if null then current active device will be targeted
     * @return - {@link Mono} with {@link CurrentPlayerState}
     * @throws IllegalStateException - if the state is empty and PlayableItem is null and nothing was specified in PlayContext
     */
    @NotNull
    Mono<CurrentPlayerState> playOrResume(@NotNull User user,
                                          @NotNull PlayCommandContext context,
                                          @Nullable TargetDevice targetDevice);

    /**
     * Pause the player playback, if playback is already paused, then do nothing
     *
     * @param user - owner of the player state
     * @return - {@link Mono} with current state of the player
     */
    @NotNull
    Mono<CurrentPlayerState> pause(@NotNull User user);

    @NotNull
    Mono<CurrentPlayerState> changeVolume(@NotNull User user, @NotNull Volume volume);

    /**
     * Seek the player's position to specified
     * @param user - owner of the player
     * @param position - position to seek position to
     * @return - updated player state
     */
    @NotNull
    Mono<CurrentPlayerState> seekToPosition(@NotNull User user,
                                            @NotNull SeekPosition position);

    /**
     * Alias for  #changeShuffle(User, true) method call
     */
    @NotNull
    default Mono<CurrentPlayerState> enableShuffle(@NotNull final User user) {
        return changeShuffle(user, ShuffleMode.ENABLED);
    }

    /**
     * Alias for  #changeShuffle(User, false) method call
     */
    @NotNull
    default Mono<CurrentPlayerState> disableShuffle(@NotNull final User user) {
        return changeShuffle(user, ShuffleMode.OFF);
    }
}
