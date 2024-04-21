package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public final class HardCodedPauseCommandPreExecutingIntegrityValidator implements PauseCommandPreExecutingIntegrityValidator {

    @Override
    @NotNull
    public Mono<PlayerCommandIntegrityValidationResult> validate(@NotNull PlayerStateEntity currentState) {
        boolean hasActiveDevice = currentState.getDevicesEntity().hasActiveDevice();

        if ( !hasActiveDevice ) {
            return Mono.just(
                    PlayerCommandIntegrityValidationResult.invalid(new NoActiveDeviceException())
            );
        }

        return Mono.just(PlayerCommandIntegrityValidationResult.valid());
    }
}
