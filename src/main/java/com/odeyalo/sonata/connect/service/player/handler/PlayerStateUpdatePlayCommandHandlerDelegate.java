package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.exception.PlayableItemNotFoundException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.player.PlayerStateService;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.validation.PlayCommandPreExecutingIntegrityValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Just updates the player state and returns it
 */
@Component
public class PlayerStateUpdatePlayCommandHandlerDelegate implements PlayCommandHandlerDelegate {
    private final PlayerStateService playerStateService;
    private final PlayableItemLoader playableItemLoader;
    private final PlayCommandPreExecutingIntegrityValidator integrityValidator;

    public PlayerStateUpdatePlayCommandHandlerDelegate(final PlayableItemLoader playableItemLoader,
                                                       final PlayCommandPreExecutingIntegrityValidator integrityValidator,
                                                       final PlayerStateService playerStateService) {
        this.playableItemLoader = playableItemLoader;
        this.integrityValidator = integrityValidator;
        this.playerStateService = playerStateService;
    }

    @Override
    @NotNull
    public Mono<CurrentPlayerState> playOrResume(@NotNull final User user,
                                                 @NotNull final PlayCommandContext context,
                                                 @Nullable final TargetDevice targetDevice) {
        return playerStateService.loadPlayerState(user)
                .flatMap(state -> validateCommand(context, state))
                .flatMap(state -> executeCommand(context, state));
    }

    @NotNull
    private Mono<CurrentPlayerState> executeCommand(@NotNull final PlayCommandContext playback,
                                                    @NotNull final CurrentPlayerState state) {

        if ( playback.shouldBeResumed() ) {
            return resumePlayback(state);
        }

        return playableItemLoader.loadPlayableItem(playback.getContextUri())
                .switchIfEmpty(Mono.defer(() -> Mono.error(PlayableItemNotFoundException.defaultException())))
                .flatMap(item -> play(state, item));
    }

    @NotNull
    private Mono<CurrentPlayerState> play(@NotNull final CurrentPlayerState player,
                                          @NotNull final PlayableItem item) {

        return playerStateService.save(
                player.play(item)
        );
    }

    @NotNull
    private Mono<CurrentPlayerState> resumePlayback(@NotNull final CurrentPlayerState player) {

        return playerStateService.save(
                player.resumePlayback()
        );
    }

    @NotNull
    private Mono<CurrentPlayerState> validateCommand(@NotNull final PlayCommandContext context,
                                                     @NotNull final CurrentPlayerState state) {
        return integrityValidator.validate(context, state)
                .thenReturn(state);
    }
}
