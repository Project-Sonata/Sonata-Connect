package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.exception.NoActiveDeviceException;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import reactor.core.publisher.Mono;

/**
 * Support interface that handle Play command and invoked from {@link BasicPlayerOperations#playOrResume(User, PlayCommandContext, TargetDevice)}
 */
public interface PlayCommandHandlerDelegate {
    /**
     * Play or resume track or episode(or any PlayableItem) playback. Return the updated state as result
     * @param user - current user to start play
     * @param context - context with play request payload
     * @param targetDevice - device to start playing to
     * @throws ReasonCodeAwareMalformedContextUriException - if context contains invalid context-uri
     * @throws NoActiveDeviceException - if state for the user does not contain active device.
     * @return - updated CurrentPlayerState
     */
    Mono<CurrentPlayerState> playOrResume(User user, PlayCommandContext context, TargetDevice targetDevice);
}
