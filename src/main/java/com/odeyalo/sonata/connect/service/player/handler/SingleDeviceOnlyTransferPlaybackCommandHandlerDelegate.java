package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.exception.DeviceNotFoundException;
import com.odeyalo.sonata.connect.exception.MultipleTargetDevicesNotSupportedException;
import com.odeyalo.sonata.connect.exception.SingleTargetDeactivationDeviceRequiredException;
import com.odeyalo.sonata.connect.exception.TargetDeviceRequiredException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.SwitchDeviceCommandArgs;
import com.odeyalo.sonata.connect.service.player.TargetDeactivationDevices;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.sync.TargetDevices;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
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
    private final PlayerStateRepository playerStateRepository;
    private final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport;

    public SingleDeviceOnlyTransferPlaybackCommandHandlerDelegate(PlayerStateRepository playerStateRepository,
                                                                  PlayerState2CurrentPlayerStateConverter playerStateConverterSupport) {
        this.playerStateRepository = playerStateRepository;
        this.playerStateConverterSupport = playerStateConverterSupport;
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

        return playerStateRepository.findByUserId(user.getId())
                .flatMap(state -> delegateTransferPlayback(targetDevices, state))
                .map(playerStateConverterSupport::convertTo);
    }

    @NotNull
    private Mono<PlayerStateEntity> delegateTransferPlayback(@NotNull final TargetDevices targetDevices,
                                                             @NotNull final PlayerStateEntity state) {

        final DevicesEntity connectedDevices = state.getDevicesEntity();
        final TargetDevice targetDevice = targetDevices.peekFirst();

        if ( connectedDevices.hasDevice(targetDevice) ) {
            return doTransferPlayback(state, targetDevice, connectedDevices);
        }

        return Mono.error(DeviceNotFoundException.defaultException());
    }

    @NotNull
    private Mono<PlayerStateEntity> doTransferPlayback(@NotNull final PlayerStateEntity state,
                                                       @NotNull final TargetDevice deviceToTransferPlayback,
                                                       @NotNull final DevicesEntity connectedDevices) {

        final var updatedDevices = connectedDevices.transferPlayback(deviceToTransferPlayback);

        return playerStateRepository.save(
                state.setDevicesEntity(updatedDevices)
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