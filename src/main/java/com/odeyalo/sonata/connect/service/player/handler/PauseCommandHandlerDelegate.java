package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface PauseCommandHandlerDelegate {

    @NotNull
    Mono<CurrentPlayerState> pause(@NotNull User user);

}
