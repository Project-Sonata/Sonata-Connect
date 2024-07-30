package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Contract to validate pause command before its being executes
 */
public interface PauseCommandPreExecutingIntegrityValidator {
    /**
     * Validate the state before executing the pause command
     * @param currentState - current state associated with user
     * @return - {@link Mono} with {@link Void} on success, or {@link Mono#error(Throwable)} with an error
     */
    @NotNull
    Mono<Void> validate(@NotNull CurrentPlayerState currentState);

}
