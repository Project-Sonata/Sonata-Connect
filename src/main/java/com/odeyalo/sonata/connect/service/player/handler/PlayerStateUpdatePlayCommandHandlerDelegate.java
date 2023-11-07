package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.common.context.ContextUriParser;
import com.odeyalo.sonata.common.context.MalformedContextUriException;
import com.odeyalo.sonata.connect.entity.CommonPlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.PlayerStateRepository;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemResolver;
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
    private final ContextUriParser contextUriParser;
    private final PlayableItemResolver playableItemResolver;
    private final PlayCommandPreExecutingIntegrityValidator integrityValidator;

    public PlayerStateUpdatePlayCommandHandlerDelegate(PlayerStateRepository playerStateRepository,
                                                       PlayerState2CurrentPlayerStateConverter playerStateConverterSupport,
                                                       ContextUriParser contextUriParser,
                                                       PlayableItemResolver playableItemResolver,
                                                       PlayCommandPreExecutingIntegrityValidator integrityValidator) {
        this.playerStateRepository = playerStateRepository;
        this.playerStateConverterSupport = playerStateConverterSupport;
        this.contextUriParser = contextUriParser;
        this.playableItemResolver = playableItemResolver;
        this.integrityValidator = integrityValidator;
    }

    @Override
    public Mono<CurrentPlayerState> playOrResume(User user, PlayCommandContext context, TargetDevice targetDevice) {
        return playerStateRepository.findByUserId(user.getId())
                .flatMap((state) -> validateCommand(context, state))
                .flatMap((state) -> saveOrError(context, state))
                .map(playerStateConverterSupport::convertTo);
    }

    private Mono<PlayerState> saveOrError(PlayCommandContext context, PlayerState state) {
        try {
            return save(context, state);
        } catch (MalformedContextUriException e) {
            return Mono.error(wrapException(context, e));
        }
    }

    private Mono<PlayerState> save(PlayCommandContext context, PlayerState state) throws MalformedContextUriException {
        ContextUri contextUri = contextUriParser.parse(context.getContextUri());
        return playableItemResolver.resolveItem(contextUri, context, state)
                .flatMap(item -> updateAndSave(state, item));
    }

    private Mono<PlayerState> updateAndSave(PlayerState state, PlayableItem item) {
        CommonPlayableItemEntity playingItem = CommonPlayableItemEntity.of(item.getId(), item.getItemType());
        state.setCurrentlyPlayingItem(playingItem);
        state.setPlayingType(PlayingType.valueOf(item.getItemType().name()));
        state.setPlaying(true);
        return playerStateRepository.save(state);
    }

    @NotNull
    private Mono<PlayerState> validateCommand(PlayCommandContext context, PlayerState state) {
        return integrityValidator.validate(context, state)
                .flatMap(result -> result.isValid() ? Mono.just(state) : Mono.error(result.getOccurredException()));
    }

    @NotNull
    private static ReasonCodeAwareMalformedContextUriException wrapException(PlayCommandContext context, MalformedContextUriException e) {
        return new ReasonCodeAwareMalformedContextUriException("Context uri is malformed", e, context.getContextUri());
    }
}
