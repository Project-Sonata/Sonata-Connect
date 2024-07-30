package com.odeyalo.sonata.connect.service.player.support.validation;

import com.odeyalo.sonata.connect.exception.IllegalCommandStateException;
import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * PlayCommandPreExecutingIntegrityValidator implementation that hardcoded to written conditions in class.
 * <p>
 *
 * Rules applied:
 * - at least one connected device should present
 * - if {@link PlayCommandContext} missing a {@link com.odeyalo.sonata.common.context.ContextUri} then a {@link CurrentPlayerState} should contain playable item
 * <p>
 * It can be used in tests
 */
@Component
public final class HardcodedPlayCommandPreExecutingIntegrityValidator implements PlayCommandPreExecutingIntegrityValidator {

    @Override
    @NotNull
    public Mono<Void> validate(@NotNull final PlayCommandContext playback,
                               @NotNull final CurrentPlayerState playerState) {

        if ( playerState.missingActiveDevice() ) {
            return Mono.error(NoActiveDeviceException::defaultException);
        }

        if ( playback.shouldBeResumed() && playerState.missingPlayingItem() ) {
            return Mono.error(() -> IllegalCommandStateException.withCustomMessage("Player command failed: Nothing is playing now and context is null!"));
        }

        return Mono.empty();
    }
}
