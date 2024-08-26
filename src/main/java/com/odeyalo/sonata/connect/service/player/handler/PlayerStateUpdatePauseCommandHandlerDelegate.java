package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.service.player.PlayerStateService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public final class PlayerStateUpdatePauseCommandHandlerDelegate implements PauseCommandHandlerDelegate {
    private final PlayerStateService playerStateService;


    @Override
    @NotNull
    public Mono<CurrentPlayerState> pause(@NotNull final User user) {
        return playerStateService.loadPlayerState(user)
                .map(CurrentPlayerState::pause)
                .flatMap(playerStateService::save);
    }
}
