package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
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
    public Mono<PlayerCommandIntegrityValidationResult> validate(PlayCommandContext context, PlayerState currentState) {
        if ( currentState.getDevicesEntity().size() == 0 ) {
            NoActiveDeviceException ex = new NoActiveDeviceException("There is no active device");
            return Mono.just(PlayerCommandIntegrityValidationResult.invalid(ex));
        }
        if ( (context == null || context.getContextUri() == null) && currentState.getCurrentlyPlayingItem() == null ) {
            IllegalStateException ex = new IllegalStateException("Nothing is playing now and context is null!");
            return Mono.just(PlayerCommandIntegrityValidationResult.invalid(ex));
        }
        return Mono.just(PlayerCommandIntegrityValidationResult.valid());
    }
}
