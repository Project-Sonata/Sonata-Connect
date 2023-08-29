package com.odeyalo.sonata.connect.service.player.support.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * A simple result wrapper about play command integrity validation status.
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class PlayCommandIntegrityValidationResult {
    boolean valid;
    // Contains info why this play command is invalid
    Throwable occurredException;

    /**
     * Create passed result and return it
     * @return - passed result
     */
    public static PlayCommandIntegrityValidationResult valid() {
        return of(true, null);
    }

    /**
     * Create result about command that don't pass
     * @param ex - exception to wrap and return
     * @return - wrapped failed result with exception
     */
    public static PlayCommandIntegrityValidationResult invalid(Throwable ex) {
        return of(false, ex);
    }
}
