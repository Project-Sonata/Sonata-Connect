package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.PlayableItemDto;
import com.odeyalo.sonata.connect.dto.TrackItemDto;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.TrackItemSpec;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlayableItem2PlayableItemDtoConverter implements Converter<PlayableItem, PlayableItemDto> {

    @Override
    public PlayableItemDto convertTo(PlayableItem playableItem) {
        if ( playableItem == null ) {
            return null;
        }
        if ( (playableItem instanceof TrackItemSpec trackSpec) ) {
            return TrackItemDto.builder()
                    .id(trackSpec.getId())
                    .name(trackSpec.getName())
                    .durationMs(trackSpec.getDuration().asMilliseconds())
                    .contextUri(trackSpec.getContextUri().asString())
                    .explicit(trackSpec.isExplicit())
                    .build();
        }
        throw new UnsupportedOperationException(String.format("PlayableItem2PlayableItemDtoConverter does not support: %s", playableItem.getItemType()));
    }
}
