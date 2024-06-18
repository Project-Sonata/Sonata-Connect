package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.common.context.MalformedContextUriException;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemLoader;
import com.odeyalo.sonata.connect.service.player.support.validation.PlayCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.support.mapper.PlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Just updates the player state and returns it
 */
@Component
public class PlayerStateUpdatePlayCommandHandlerDelegate implements PlayCommandHandlerDelegate {
    private final PlayerStateRepository playerStateRepository;
    private final PlayerState2CurrentPlayerStateConverter playerStateConverterSupport;
    private final PlayableItemLoader playableItemLoader;
    private final PlayCommandPreExecutingIntegrityValidator integrityValidator;
    private final TrackItemEntity.Factory factory = new TrackItemEntity.Factory();

    public PlayerStateUpdatePlayCommandHandlerDelegate(PlayerStateRepository playerStateRepository,
                                                       PlayerState2CurrentPlayerStateConverter playerStateConverterSupport,
                                                       PlayableItemLoader playableItemLoader,
                                                       PlayCommandPreExecutingIntegrityValidator integrityValidator) {
        this.playerStateRepository = playerStateRepository;
        this.playerStateConverterSupport = playerStateConverterSupport;
        this.playableItemLoader = playableItemLoader;
        this.integrityValidator = integrityValidator;
    }

    @Override
    public Mono<CurrentPlayerState> playOrResume(User user, PlayCommandContext context, TargetDevice targetDevice) {
        return playerStateRepository.findByUserId(user.getId())
                .flatMap(state -> validateCommand(context, state))
                .flatMap(state -> save(context, state))
                .map(playerStateConverterSupport::convertTo);
    }

    private Mono<PlayerStateEntity> save(PlayCommandContext context, PlayerStateEntity state) throws MalformedContextUriException {
        ContextUri contextUri = ContextUri.fromString(context.getContextUri());

        return playableItemLoader.resolveItem(contextUri)
                .flatMap(item -> updateAndSavePlayerState(state, item));
    }

    private Mono<PlayerStateEntity> updateAndSavePlayerState(PlayerStateEntity state, PlayableItem item) {
        PlayableItemEntity playableItemEntity = factory.create(item);
        state.playOrResume(playableItemEntity);
        return playerStateRepository.save(state);
    }

    @NotNull
    private Mono<PlayerStateEntity> validateCommand(PlayCommandContext context, PlayerStateEntity state) {
        return integrityValidator.validate(context, state)
                .flatMap(result -> result.isValid() ? Mono.just(state) : Mono.error(result.getOccurredException()));
    }
}
