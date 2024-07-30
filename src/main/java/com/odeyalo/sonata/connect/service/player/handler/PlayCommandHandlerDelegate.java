package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * Support interface that handle Play command and invoked from {@link BasicPlayerOperations#playOrResume(User, PlayCommandContext, TargetDevice)}
 */
public interface PlayCommandHandlerDelegate {
    /**
     * Play or resume player playback based on {@link PlayCommandContext}.
     * Return the updated state as result
     *
     * @param user         - current user to start play
     * @param context      - context with play request payload
     * @param targetDevice - device to start playing to, if null supplied, then currently active device will start play
     * @return - updated {@link CurrentPlayerState}
     * @throws ReasonCodeAwareMalformedContextUriException - if context contains invalid context-uri
     * @throws NoActiveDeviceException                     - if state for the user does not contain active device.
     * @throws com.odeyalo.sonata.connect.exception.PlayerCommandException - if the command is malformed
     */
    @NotNull
    Mono<CurrentPlayerState> playOrResume(@NotNull User user,
                                          @NotNull PlayCommandContext context,
                                          @Nullable TargetDevice targetDevice);
}
