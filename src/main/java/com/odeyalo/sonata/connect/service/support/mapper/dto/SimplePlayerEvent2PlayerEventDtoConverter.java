package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.CommonPlayerEventDto;
import com.odeyalo.sonata.connect.dto.PlayerEventDto;
import com.odeyalo.sonata.connect.dto.PlayerStateDto;
import com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Just wraps the incoming events to {@link CommonPlayerEventDto}
 */
@Component
public final class SimplePlayerEvent2PlayerEventDtoConverter implements Converter<PlayerEvent, PlayerEventDto> {
    private final CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter;

    @Autowired
    public SimplePlayerEvent2PlayerEventDtoConverter(CurrentPlayerState2PlayerStateDtoConverter playerState2PlayerStateDtoConverter) {
        this.playerState2PlayerStateDtoConverter = playerState2PlayerStateDtoConverter;
    }

    @Override
    public PlayerEventDto convertTo(PlayerEvent event) {
        PlayerStateDto stateDto = playerState2PlayerStateDtoConverter.convertTo(event.getCurrentPlayerState());
        return new CommonPlayerEventDto(stateDto, event.getDeviceThatChanged(), event.getEventType());
    }
}
