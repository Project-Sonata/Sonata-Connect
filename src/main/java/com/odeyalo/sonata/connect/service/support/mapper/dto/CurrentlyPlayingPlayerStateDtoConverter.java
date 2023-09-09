package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.CurrentlyPlayingPlayerStateDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.dto.PlayableItemDto;
import com.odeyalo.sonata.connect.model.CurrentlyPlayingPlayerState;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convert CurrentlyPlayingPlayerState to CurrentlyPlayingPlayerStateDto
 */
@Component
public class CurrentlyPlayingPlayerStateDtoConverter implements Converter<CurrentlyPlayingPlayerState, CurrentlyPlayingPlayerStateDto> {
    private final PlayableItem2PlayableItemDtoConverter playableItemDtoConverterSupport;
    private final Devices2DevicesDtoConverter devicesDtoConverterSupport;

    @Autowired
    public CurrentlyPlayingPlayerStateDtoConverter(PlayableItem2PlayableItemDtoConverter playableItemDtoConverterSupport, Devices2DevicesDtoConverter devicesDtoConverterSupport) {
        this.playableItemDtoConverterSupport = playableItemDtoConverterSupport;
        this.devicesDtoConverterSupport = devicesDtoConverterSupport;
    }

    @Override
    public CurrentlyPlayingPlayerStateDto convertTo(CurrentlyPlayingPlayerState state) {
        return CurrentlyPlayingPlayerStateDto.builder()
                .playing(state.isPlaying())
                .shuffleState(state.getShuffleState())
                .repeatState(state.getRepeatState())
                .currentlyPlayingType(state.getCurrentlyPlayingType())
                .currentlyPlayingItem(convertToPlayableItem(state))
                .devices(convertDevices(state))
                .build();
    }

    private DevicesDto convertDevices(CurrentlyPlayingPlayerState state) {
        return devicesDtoConverterSupport.convertTo(state.getDevices());
    }

    private PlayableItemDto convertToPlayableItem(CurrentlyPlayingPlayerState state) {
        return playableItemDtoConverterSupport.convertTo(state.getPlayableItem());
    }
}
