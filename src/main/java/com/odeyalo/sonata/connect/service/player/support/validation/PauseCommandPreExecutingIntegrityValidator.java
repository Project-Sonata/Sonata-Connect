package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Contract to validate pause command data before its being executes
 */
public interface PauseCommandPreExecutingIntegrityValidator {
    /**
     * Validate the given parameters, returns result based on this
     * @param currentState - current state associated with user
     * @return - {@link Mono with {@link PlayerCommandIntegrityValidationResult#valid()} if context is valid,
     * otherwise {@link Mono} with {@link PlayerCommandIntegrityValidationResult#invalid}
     */
    @NotNull
    Mono<PlayerCommandIntegrityValidationResult> validate(@NotNull PlayerStateEntity currentState);

}
