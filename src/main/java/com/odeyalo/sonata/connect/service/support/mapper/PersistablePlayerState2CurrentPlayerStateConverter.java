package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.Devices;
import com.odeyalo.sonata.connect.entity.PlayableItemEntity;
import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.DevicesModel;
import com.odeyalo.sonata.connect.model.PlayableItem;
import com.odeyalo.sonata.connect.repository.storage.PersistablePlayerState;
import org.springframework.stereotype.Component;

/**
 * Convert PersistablePlayerState entity to CurrentPlayerState
 */
@Component
public class PersistablePlayerState2CurrentPlayerStateConverter implements Converter<PersistablePlayerState, CurrentPlayerState> {
    private final DevicesToDevicesModelConverter devicesConverterSupport;
    private final PlayableItemEntity2PlayableItemConverter playableItemConverterSupport;

    public PersistablePlayerState2CurrentPlayerStateConverter(DevicesToDevicesModelConverter devicesConverter,
                                                              PlayableItemEntity2PlayableItemConverter playableItemConverter) {
        this.devicesConverterSupport = devicesConverter;
        this.playableItemConverterSupport = playableItemConverter;
    }

    @Override
    public CurrentPlayerState convertTo(PersistablePlayerState state) {
        return CurrentPlayerState.builder()
                .id(state.getId())
                .playingType(state.getPlayingType())
                .playing(state.isPlaying())
                .shuffleState(state.getShuffleState())
                .progressMs(state.getProgressMs())
                .repeatState(state.getRepeatState())
                .devices(toDevicesModel(state.getDevices()))
                .playableItem(toPlayableItem(state))
                .build();
    }

    private PlayableItem toPlayableItem(PersistablePlayerState state) {
        PlayableItemEntity item = state.getCurrentlyPlayingItem();
        return item != null ? playableItemConverterSupport.convertTo(item) : null;
    }

    private DevicesModel toDevicesModel(Devices devices) {
        return devicesConverterSupport.convertTo(devices);
    }
}
