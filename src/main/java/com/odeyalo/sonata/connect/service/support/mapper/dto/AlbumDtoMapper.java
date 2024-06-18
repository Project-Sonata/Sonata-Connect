package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.AlbumDto;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {
        ArtistListDtoMapper.class,
        ImageListDtoMapper.class
}, injectionStrategy = CONSTRUCTOR)
public interface AlbumDtoMapper {

    @Mapping(target = "id", expression = "java( source.getId().value() )")
    @Mapping(target = "contextUri", expression = "java( source.getContextUri().asString() )")
    AlbumDto toAlbumDto(AlbumSpec source);

}
