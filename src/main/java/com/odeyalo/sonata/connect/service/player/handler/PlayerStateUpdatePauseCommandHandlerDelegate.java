package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.support.validation.PauseCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public final class PlayerStateUpdatePauseCommandHandlerDelegate implements PauseCommandHandlerDelegate {
    private final PlayerStateRepository playerStateRepository;
    private final PauseCommandPreExecutingIntegrityValidator preExecutingIntegrityValidator;
    private final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport;

    @Override
    @NotNull
    public Mono<CurrentPlayerState> pause(@NotNull User user) {
        return playerStateRepository.findByUserId(user.getId())
                .flatMap(state -> validate(state))
                .map(PlayerState::pause)
                .flatMap(playerStateRepository::save)
                .map(playerStateConverterSupport::convertTo);
    }

    @NotNull
    private Mono<PlayerState> validate(PlayerState state) {
        return preExecutingIntegrityValidator.validate(state)
                .flatMap(validationResult -> {
                    if ( validationResult.isValid() ) {
                        return Mono.just(state);
                    }
                    return Mono.error(validationResult.getOccurredException());
                });
    }
}
