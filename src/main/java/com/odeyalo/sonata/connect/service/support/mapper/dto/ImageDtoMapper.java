package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.ImageDto;
import com.odeyalo.sonata.connect.model.track.ImageSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ImageDtoMapper {

    @Mapping(target = "url", expression = "java( source.getUrl().toString() )")
    ImageDto toImageDto(ImageSpec source);

}
