package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convert {@link CurrentPlayerState} to {@link CurrentlyPlayingPlayerState}
 */
@Mapper(componentModel = "spring")
public interface CurrentPlayerState2CurrentlyPlayingPlayerStateConverter extends Converter<CurrentPlayerState, CurrentlyPlayingPlayerState> {


    @Mapping(source = "playingType", target = "currentlyPlayingType")
    CurrentlyPlayingPlayerState convertTo(CurrentPlayerState source);
}
