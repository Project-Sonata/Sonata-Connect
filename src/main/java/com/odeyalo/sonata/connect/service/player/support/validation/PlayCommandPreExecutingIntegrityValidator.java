package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Contract to validate play or resume command before it is being executed
 */
public interface PlayCommandPreExecutingIntegrityValidator {
    /**
     * Validate the given arguments before executing play or resume playback command
     *
     * @param context      - a play command context that contains info about command
     * @param currentState - current state associated with user, before executing this command
     * @return - {@link Mono} with the {@link Void} on success or {@link  Mono#error(Throwable) } with an error that occurred
     */
    @NotNull
    Mono<Void> validate(@NotNull PlayCommandContext context,
                        @NotNull CurrentPlayerState currentState);
}
