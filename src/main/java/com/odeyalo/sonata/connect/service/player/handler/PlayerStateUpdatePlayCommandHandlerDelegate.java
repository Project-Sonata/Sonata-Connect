package com.odeyalo.sonata.connect.service.player.handler;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.common.context.ContextUriParser;
import com.odeyalo.sonata.common.context.MalformedContextUriException;
import com.odeyalo.sonata.connect.entity.CommonPlayableItemEntity;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.exception.ReasonCodeAwareMalformedContextUriException;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.model.User;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import com.odeyalo.sonata.connect.repository.storage.PlayerStateStorage;
import com.odeyalo.sonata.connect.service.player.PlayCommandContext;
import com.odeyalo.sonata.connect.service.player.TargetDevice;
import com.odeyalo.sonata.connect.service.player.support.PlayableItemResolver;
import com.odeyalo.sonata.connect.service.player.support.validation.PlayCommandPreExecutingIntegrityValidator;
import com.odeyalo.sonata.connect.service.support.mapper.PersistablePlayerState2CurrentPlayerStateConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Just updates the player state and returns it
 */
@Component
public class PlayerStateUpdatePlayCommandHandlerDelegate implements PlayCommandHandlerDelegate {
    private final PlayerStateStorage playerStateStorage;
    private final PersistablePlayerState2CurrentPlayerStateConverter playerStateConverterSupport;
    private final ContextUriParser contextUriParser;
    private final PlayableItemResolver playableItemResolver;
    private final PlayCommandPreExecutingIntegrityValidator integrityValidator;

    public PlayerStateUpdatePlayCommandHandlerDelegate(PlayerStateStorage playerStateStorage,
                                                       PersistablePlayerState2CurrentPlayerStateConverter playerStateConverterSupport,
                                                       ContextUriParser contextUriParser,
                                                       PlayableItemResolver playableItemResolver,
                                                       PlayCommandPreExecutingIntegrityValidator integrityValidator) {
        this.playerStateStorage = playerStateStorage;
        this.playerStateConverterSupport = playerStateConverterSupport;
        this.contextUriParser = contextUriParser;
        this.playableItemResolver = playableItemResolver;
        this.integrityValidator = integrityValidator;
    }

    @Override
    public Mono<CurrentPlayerState> playOrResume(User user, PlayCommandContext context, TargetDevice targetDevice) {
        return playerStateStorage.findByUserId(user.getId())
                .flatMap((state) -> validateCommand(context, state))
                .flatMap((state) -> saveOrError(context, state))
                .map(playerStateConverterSupport::convertTo);
    }

    private Mono<PersistablePlayerState> saveOrError(PlayCommandContext context, PersistablePlayerState state) {
        try {
            return save(context, state);
        } catch (MalformedContextUriException e) {
            return Mono.error(wrapException(context, e));
        }
    }

    private Mono<PersistablePlayerState> save(PlayCommandContext context, PersistablePlayerState state) throws MalformedContextUriException {
        ContextUri contextUri = contextUriParser.parse(context.getContextUri());
        return playableItemResolver.resolveItem(contextUri, context, state)
                .flatMap(item -> updateAndSave(state, item));
    }

    private Mono<PersistablePlayerState> updateAndSave(PersistablePlayerState state, PlayableItem item) {
        CommonPlayableItemEntity playingItem = CommonPlayableItemEntity.of(item.getId(), item.getItemType());
        state.setCurrentlyPlayingItem(playingItem);
        state.setPlayingType(PlayingType.valueOf(item.getItemType().name()));
        state.setPlaying(true);
        return playerStateStorage.save(state);
    }

    @NotNull
    private Mono<PersistablePlayerState> validateCommand(PlayCommandContext context, PersistablePlayerState state) {
        return integrityValidator.validate(context, state)
                .flatMap(result -> result.isValid() ? Mono.just(state) : Mono.error(result.getOccurredException()));
    }

    @NotNull
    private static ReasonCodeAwareMalformedContextUriException wrapException(PlayCommandContext context, MalformedContextUriException e) {
        return new ReasonCodeAwareMalformedContextUriException("Context uri is malformed", e, context.getContextUri());
    }
}
