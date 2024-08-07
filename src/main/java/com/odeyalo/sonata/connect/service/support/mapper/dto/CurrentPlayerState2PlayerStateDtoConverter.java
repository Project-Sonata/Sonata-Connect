package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.ShuffleMode;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {
                PlayableItem2PlayableItemDtoConverter.class,
                Devices2DevicesDtoConverter.class
        },
        imports = {
                ShuffleMode.class
        })
public interface CurrentPlayerState2PlayerStateDtoConverter extends Converter<CurrentPlayerState, PlayerStateDto> {

    @Mapping(source = "playing", target = "isPlaying")
    @Mapping(target = "currentlyPlayingType",
            expression = "java(currentPlayerState.getPlayingType() != null ?" +
                    " currentPlayerState.getPlayingType().toString().toLowerCase() " +
                    ": null)")
    @Mapping(target = "shuffleState", expression = "java( currentPlayerState.getShuffleState() == ShuffleMode.ENABLED )")
    @Mapping(target = "volume", expression = "java( currentPlayerState.getVolume().asInt() )")
    PlayerStateDto convertTo(CurrentPlayerState currentPlayerState);
}