package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.common.context.MalformedContextUriException;
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
    public Mono<CurrentPlayerState> playOrResume(@NotNull final User user,
                                                 @Nullable final PlayCommandContext context,
                                                 @Nullable final TargetDevice targetDevice) {
        return playerStateService.loadPlayerState(user)
                .flatMap(state -> validateCommand(context, state))
                .flatMap(state -> save(context, state));
    }

    @NotNull
    private Mono<CurrentPlayerState> save(PlayCommandContext context, CurrentPlayerState state) throws MalformedContextUriException {
        ContextUri contextUri = ContextUri.fromString(context.getContextUri());

        return playableItemLoader.loadPlayableItem(contextUri)
                .flatMap(item -> updateAndSavePlayerState(state, item));
    }

    @NotNull
    private Mono<CurrentPlayerState> updateAndSavePlayerState(CurrentPlayerState state, PlayableItem item) {

        return playerStateService.save(
                state.playOrResume(item)
        );
    }

    @NotNull
    private Mono<CurrentPlayerState> validateCommand(@NotNull final PlayCommandContext context,
                                                     @NotNull final CurrentPlayerState state) {
        return integrityValidator.validate(context, state)
                .flatMap(result -> result.isValid() ? Mono.just(state) : Mono.error(result.getOccurredException()));
    }
}
