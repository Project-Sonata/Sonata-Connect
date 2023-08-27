package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.dto.PlayableItemDto;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.DevicesModel;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.model.PlayingType;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentPlayerState2PlayerStateDtoConverter implements Converter<CurrentPlayerState, PlayerStateDto> {
    private final PlayableItem2PlayableItemDtoConverter playableItemDtoConverterSupport;
    private final DevicesModel2DevicesDtoConverter devicesDtoConverterSupport;

    @Autowired
    public CurrentPlayerState2PlayerStateDtoConverter(PlayableItem2PlayableItemDtoConverter playableItemDtoConverterSupport,
                                                      DevicesModel2DevicesDtoConverter devicesDtoConverterSupport) {
        this.playableItemDtoConverterSupport = playableItemDtoConverterSupport;
        this.devicesDtoConverterSupport = devicesDtoConverterSupport;
    }

    @Override
    public PlayerStateDto convertTo(CurrentPlayerState state) {
        return PlayerStateDto.builder()
                .currentlyPlayingType(playingTypeOrNull(state))
                .isPlaying(state.isPlaying())
                .repeatState(state.getRepeatState())
                .progressMs(state.getProgressMs())
                .devices(toDevicesDto(state.getDevices()))
                .shuffleState(state.getShuffleState())
                .playingItem(toPlayableItemOrNull(state))
                .build();
    }

    private DevicesDto toDevicesDto(DevicesModel devices) {
        return devicesDtoConverterSupport.convertTo(devices);
    }

    private PlayableItemDto toPlayableItemOrNull(CurrentPlayerState state) {
        PlayableItem item = state.getPlayableItem();
        return item != null ? playableItemDtoConverterSupport.convertTo(item) : null;
    }

    private static String playingTypeOrNull(CurrentPlayerState state) {

        PlayingType playingType = state.getPlayingType();

        return playingType != null ? playingType.name().toLowerCase() : null;
    }
}
