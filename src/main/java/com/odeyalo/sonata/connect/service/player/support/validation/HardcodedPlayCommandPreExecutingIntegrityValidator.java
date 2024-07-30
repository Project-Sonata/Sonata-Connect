package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * PlayCommandPreExecutingIntegrityValidator implementation that hardcoded to written conditions in class.
 * <p>
 * It can be used in tests
 */
@Component
public class HardcodedPlayCommandPreExecutingIntegrityValidator implements PlayCommandPreExecutingIntegrityValidator {

    @Override
    public Mono<PlayerCommandIntegrityValidationResult> validate(@NotNull PlayCommandContext context, CurrentPlayerState currentState) {
        if ( currentState.getDevices().size() == 0 ) {
            NoActiveDeviceException ex = new NoActiveDeviceException("There is no active device");
            return Mono.just(PlayerCommandIntegrityValidationResult.invalid(ex));
        }

        if ( context.getContextUri() == null && currentState.getPlayingItem() == null ) {
            IllegalStateException ex = new IllegalStateException("Nothing is playing now and context is null!");
            return Mono.just(PlayerCommandIntegrityValidationResult.invalid(ex));
        }


        if ( context.getContextUri() != null && !ContextUri.isValid(context.getContextUri()) ) {
            final var exception = new ReasonCodeAwareMalformedContextUriException("Context uri is malformed", context.getContextUri());

            return Mono.just(PlayerCommandIntegrityValidationResult.invalid(exception));
        }

        return Mono.just(PlayerCommandIntegrityValidationResult.valid());
    }
}
