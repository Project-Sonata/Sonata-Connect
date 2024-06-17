package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.PlayableItemDto;
import com.odeyalo.sonata.connect.dto.TrackItemDto;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.TrackItemSpec;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
public class PlayableItem2PlayableItemDtoConverter implements Converter<PlayableItem, PlayableItemDto> {
    private final TrackItemSpec2TrackItemDtoConverter trackConverter;

    public PlayableItem2PlayableItemDtoConverter(final TrackItemSpec2TrackItemDtoConverter trackConverter) {
        this.trackConverter = trackConverter;
    }

    @Override
    public PlayableItemDto convertTo(PlayableItem playableItem) {

        if ( playableItem == null ) {
            return null;
        }

        if ( playableItem instanceof TrackItemSpec trackSpec ) {
            return trackConverter.toTrackItemDto(trackSpec);
        }

        throw new UnsupportedOperationException(String.format("PlayableItem2PlayableItemDtoConverter does not support: %s", playableItem.getItemType()));
    }

    @Mapper(componentModel = "spring")
    public interface TrackItemSpec2TrackItemDtoConverter {

        @Mapping(target = "discNumber", expression = "java( spec.getOrder().discNumber() )")
        @Mapping(target = "index", expression = "java( spec.getOrder().index() )")
        @Mapping(target = "contextUri", expression = "java( spec.getContextUri().asString() )")
        @Mapping(target = "durationMs", expression = "java( spec.getDuration().asMilliseconds() )")
        TrackItemDto toTrackItemDto(TrackItemSpec spec);

    }
}
