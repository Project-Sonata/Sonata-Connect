package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.PlayerStateEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convert {@link PlayerStateEntity} entity to {@link CurrentPlayerState}
 */
@Mapper(componentModel = "spring", uses =  {
        DevicesEntity2DevicesConverter.class,
        PlayableItemEntity2PlayableItemConverter.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PlayerState2CurrentPlayerStateConverter extends Converter<PlayerStateEntity, CurrentPlayerState> {

    @Mapping(target = "playableItem", source = "currentlyPlayingItem")
    CurrentPlayerState convertTo(PlayerStateEntity state);

}
