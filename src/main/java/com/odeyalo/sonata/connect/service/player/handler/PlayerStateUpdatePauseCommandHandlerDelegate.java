package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.PlayerStateService;
import com.odeyalo.sonata.connect.service.player.support.validation.PauseCommandPreExecutingIntegrityValidator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public final class PlayerStateUpdatePauseCommandHandlerDelegate implements PauseCommandHandlerDelegate {
    private final PauseCommandPreExecutingIntegrityValidator preExecutingIntegrityValidator;
    private final PlayerStateService playerStateService;

    @Override
    @NotNull
    public Mono<CurrentPlayerState> pause(@NotNull final User user) {
        return playerStateService.loadPlayerState(user)
                .flatMap(this::validateCommand)
                .map(CurrentPlayerState::pause)
                .flatMap(playerStateService::save);
    }

    @NotNull
    private Mono<CurrentPlayerState> validateCommand(@NotNull final CurrentPlayerState state) {
        return preExecutingIntegrityValidator.validate(state)
                .thenReturn(state);
    }
}
