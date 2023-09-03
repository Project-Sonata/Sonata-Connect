package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.DeviceConnectedPlayerEventDto;
import com.odeyalo.sonata.connect.dto.PlayerEventDto;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.dto.PlayerStateUpdatedPlayerEventDto;
import com.odeyalo.sonata.connect.service.player.sync.event.DeviceConnectedPlayerEvent;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerStateUpdatedPlayerEvent;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HardcodedPlayerEvent2PlayerEventDtoConverter implements Converter<PlayerEvent, PlayerEventDto> {
    private final CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter;

    @Autowired
    public HardcodedPlayerEvent2PlayerEventDtoConverter(CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter) {
        this.playerState2PlayerStateDtoConverter = playerState2PlayerStateDtoConverter;
    }

    @Override
    public PlayerEventDto convertTo(PlayerEvent event) {
        if (event instanceof PlayerStateUpdatedPlayerEvent e) {
            PlayerStateDto dto = playerState2PlayerStateDtoConverter.convertTo(e.getPlayerState());
            return new PlayerStateUpdatedPlayerEventDto(dto, e.getEventType(), e.getDeviceThatChanged());
        }

        if (event instanceof DeviceConnectedPlayerEvent e) {
            PlayerStateDto dto = playerState2PlayerStateDtoConverter.convertTo(e.getPlayerState());
            return DeviceConnectedPlayerEventDto.of(dto, e.getDeviceThatChanged());
        }
        return null;
    }
}
