package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.AlbumDto;
import com.odeyalo.sonata.connect.model.track.AlbumSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AlbumDtoMapper {

    @Mapping(target = "id", expression = "java( source.getId().value() )")
    AlbumDto toAlbumDto(AlbumSpec source);

}
