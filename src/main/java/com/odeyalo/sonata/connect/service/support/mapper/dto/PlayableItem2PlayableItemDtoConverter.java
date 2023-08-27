package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.PlayableItemDto;
import com.odeyalo.sonata.connect.dto.TrackItemDto;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayableItemType;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlayableItem2PlayableItemDtoConverter implements Converter<PlayableItem, PlayableItemDto> {

    @Override
    public PlayableItemDto convertTo(PlayableItem playableItem) {
        if (playableItem.getItemType() == PlayableItemType.TRACK) {
            return TrackItemDto.of(playableItem.getId());
        }
        throw new UnsupportedOperationException(String.format("PlayableItem2PlayableItemDtoConverter does not support: %s", playableItem.getItemType()));
    }
}
