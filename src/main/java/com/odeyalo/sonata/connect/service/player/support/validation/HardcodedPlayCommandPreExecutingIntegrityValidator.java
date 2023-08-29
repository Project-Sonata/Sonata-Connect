package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * PlayCommandPreExecutingIntegrityValidator implementation that hardcoded to written conditions in class.
 *
 * It can be used in tests
 */
@Component
public class HardcodedPlayCommandPreExecutingIntegrityValidator implements PlayCommandPreExecutingIntegrityValidator {

    @Override
    public Mono<PlayCommandIntegrityValidationResult> validate(PlayCommandContext context, PersistablePlayerState currentState) {
        if (currentState.getDevices().size() == 0) {
            NoActiveDeviceException ex = new NoActiveDeviceException("There is no active device");
            return Mono.just(PlayCommandIntegrityValidationResult.invalid(ex));
        }
        if ((context == null || context.getContextUri() == null) && currentState.getCurrentlyPlayingItem() == null) {
            IllegalStateException ex = new IllegalStateException("Nothing is playing now and context is null!");
            return Mono.just(PlayCommandIntegrityValidationResult.invalid(ex));
        }
        return Mono.just(PlayCommandIntegrityValidationResult.valid());
    }
}
