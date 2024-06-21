package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.factory.PlayerStateEntityFactory;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Getter
public final class PlayerStateService {
    private final PlayerStateRepository playerStateRepository;
    private final PlayerState2CurrentPlayerStateConverter playerStateConverter;
    private final PlayerStateEntityFactory playerStateFactory;

    public PlayerStateService(final PlayerStateRepository playerStateRepository,
                              final PlayerState2CurrentPlayerStateConverter playerStateConverter,
                              final PlayerStateEntityFactory playerStateFactory) {
        this.playerStateRepository = playerStateRepository;
        this.playerStateConverter = playerStateConverter;
        this.playerStateFactory = playerStateFactory;
    }

    @NotNull
    public Mono<CurrentPlayerState> loadPlayerState(@NotNull final User user) {
        return playerStateRepository.findByUserId(user.getId())
                .map(playerStateConverter::convertTo);
    }

    @NotNull
    public Mono<CurrentPlayerState> save(final CurrentPlayerState state) {
        final PlayerStateEntity playerState = playerStateFactory.create(state);

        return playerStateRepository.save(playerState)
                .map(playerStateConverter::convertTo);
    }
}
