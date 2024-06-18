package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.ArtistDto;
import com.odeyalo.sonata.connect.model.track.ArtistListSpec;
import com.odeyalo.sonata.connect.model.track.ArtistSpec;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public abstract class ArtistListDtoMapper {
    @Autowired
    ArtistDtoMapper artistMapper;

    public ArtistListDtoMapper() {
    }

    // for tests
    public ArtistListDtoMapper(final ArtistDtoMapper artistMapper) {
        this.artistMapper = artistMapper;
    }

    public List<ArtistDto> toArtistDtoList(ArtistListSpec<? extends ArtistSpec> source) {
        return source.stream().map(it -> artistMapper.toArtistDto(it)).toList();
    }
}
