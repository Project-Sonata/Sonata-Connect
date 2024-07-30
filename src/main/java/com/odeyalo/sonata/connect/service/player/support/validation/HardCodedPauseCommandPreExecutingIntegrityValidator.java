package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public final class HardCodedPauseCommandPreExecutingIntegrityValidator implements PauseCommandPreExecutingIntegrityValidator {

    @Override
    @NotNull
    public Mono<Void> validate(@NotNull final CurrentPlayerState currentState) {

        if ( currentState.missingActiveDevice() ) {
            return Mono.error(NoActiveDeviceException::defaultException);
        }

        return Mono.empty();
    }
}
