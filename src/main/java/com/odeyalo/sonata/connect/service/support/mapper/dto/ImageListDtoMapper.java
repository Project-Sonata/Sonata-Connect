package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.ImageDto;
import com.odeyalo.sonata.connect.model.track.ImageListSpec;
import com.odeyalo.sonata.connect.model.track.ImageSpec;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public abstract class ImageListDtoMapper {
    @Autowired
    ImageDtoMapper imageDtoMapper;

    public ImageListDtoMapper() {
    }

    // for tests
    public ImageListDtoMapper(final ImageDtoMapper imageDtoMapper) {
        this.imageDtoMapper = imageDtoMapper;
    }

    public List<ImageDto> toArtistDtoList(ImageListSpec<? extends ImageSpec> source) {
        return source.stream().map(it -> imageDtoMapper.toImageDto(it)).toList();
    }
}
