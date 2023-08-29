package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import reactor.core.publisher.Mono;

/**
 * Interface to validate the Play command data before its executes
 */
public interface PlayCommandPreExecutingIntegrityValidator {
    /**
     * Validate the given parameters, returns result based on this
     * @param context - context to validate
     * @param currentState - current state associated with user
     * @return - PlayCommandIntegrityValidationResult#valid if context is valid, otherwise PlayCommandIntegrityValidationResult#invalid 
     */
    Mono<PlayCommandIntegrityValidationResult> validate(PlayCommandContext context, PersistablePlayerState currentState);

}
