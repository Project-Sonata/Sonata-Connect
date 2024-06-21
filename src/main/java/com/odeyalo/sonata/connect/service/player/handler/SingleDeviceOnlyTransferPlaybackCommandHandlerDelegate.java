package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.exception.DeviceNotFoundException;
import com.odeyalo.sonata.connect.exception.MultipleTargetDevicesNotSupportedException;
import com.odeyalo.sonata.connect.exception.SingleTargetDeactivationDeviceRequiredException;
import com.odeyalo.sonata.connect.exception.TargetDeviceRequiredException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.PlayerStateService;
import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link TransferPlaybackCommandHandlerDelegate} that supports only single active device at once
 */
@Component
public class SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate implements TransferPlaybackCommandHandlerDelegate {
    private final PlayerStateService playerStateService;

    public SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate(PlayerStateService playerStateService) {
        this.playerStateService = playerStateService;
    }

    @NotNull
    private Mono<CurrentPlayerState> delegateTransferPlayback(@NotNull final TargetDevices targetDevices,
                                                              @NotNull final CurrentPlayerState state) {

        final Devices connectedDevices = state.getDevices();
        final TargetDevice targetDevice = targetDevices.peekFirst();

        if ( connectedDevices.hasDevice(targetDevice) ) {
            return doTransferPlayback(state, targetDevice, connectedDevices);
        }

        return Mono.error(DeviceNotFoundException.defaultException());
    }

    @NotNull
    @Override
    public Mono<CurrentPlayerState> transferPlayback(@NotNull final User user,
                                                     @NotNull final SwitchDeviceCommandArgs args,
                                                     @NotNull final TargetDeactivationDevices deactivationDevices,
                                                     @NotNull final TargetDevices targetDevices) {

        final TransferPlaybackPolicy policy = TransferPlaybackPolicy.basedOn(targetDevices, deactivationDevices);

        if ( policy.hasViolations() ) {
            return policy.exceptionAsReactiveStream();
        }

        return playerStateService.loadPlayerState(user)
                .flatMap(state -> delegateTransferPlayback(targetDevices, state));
    }

    @NotNull
    private Mono<CurrentPlayerState> doTransferPlayback(@NotNull final CurrentPlayerState state,
                                                        @NotNull final TargetDevice deviceToTransferPlayback,
                                                        @NotNull final Devices connectedDevices) {

        final var updatedDevices = connectedDevices.transferPlayback(deviceToTransferPlayback);

        return playerStateService.save(
                state.withDevices(updatedDevices)
        );
    }


    private record TransferPlaybackPolicy(boolean isValid, @Nullable Exception error) {

        public static TransferPlaybackPolicy basedOn(@NotNull final TargetDevices targetDevices,
                                                     @NotNull final TargetDeactivationDevices deactivationDevices) {
            final Pair<Boolean, Exception> validationResult = validate(targetDevices, deactivationDevices);

            if ( validationResult.getLeft() ) {
                return new TransferPlaybackPolicy(true, null);
            }

            return new TransferPlaybackPolicy(false, validationResult.getRight());
        }

        private static Pair<Boolean, Exception> validate(final TargetDevices targetDevices,
                                                         final TargetDeactivationDevices deactivationDevices) {
            if ( deactivationDevices.size() > 1 ) {
                return Pair.of(false, SingleTargetDeactivationDeviceRequiredException.defaultException());
            }

            if ( targetDevices.size() < 1 ) {
                return Pair.of(false, TargetDeviceRequiredException.defaultException());
            }

            if ( targetDevices.size() > 1 ) {
                return Pair.of(false, MultipleTargetDevicesNotSupportedException.defaultException());
            }

            return Pair.of(true, null);
        }

        public boolean hasViolations() {
            return !isValid;
        }

        public <T> Mono<T> exceptionAsReactiveStream() {
            if ( error == null ) {
                throw new IllegalStateException("This method should not be called if policy has not violations!");
            }

            return Mono.error(error);
        }
    }
}