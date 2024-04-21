package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.CurrentlyPlayingPlayerStateDto;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convert {@link CurrentlyPlayingPlayerState} to {@link CurrentlyPlayingPlayerStateDto}
 */
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {
        PlayableItem2PlayableItemDtoConverter.class,
        Devices2DevicesDtoConverter.class
})
public interface CurrentlyPlayingPlayerStateDtoConverter extends Converter<CurrentlyPlayingPlayerState, CurrentlyPlayingPlayerStateDto> {

    @Mapping(source = "playableItem", target = "currentlyPlayingItem")
    CurrentlyPlayingPlayerStateDto convertTo(CurrentlyPlayingPlayerState currentlyPlayingPlayerState);
}
