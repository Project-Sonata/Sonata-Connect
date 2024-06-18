package com.odeyalo.sonata.connect.service.support.mapper.dto;


import com.odeyalo.sonata.connect.dto.ArtistDto;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ArtistDtoMapper {

    @Mapping(target = "id", expression = "java( source.getId().value() )")
    @Mapping(target = "contextUri", expression = "java( source.getContextUri().asString() )")
    ArtistDto toArtistDto(ArtistSpec source);

}
