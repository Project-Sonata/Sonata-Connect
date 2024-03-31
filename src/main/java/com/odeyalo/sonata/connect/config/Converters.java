package com.odeyalo.sonata.connect.config;

import com.odeyalo.sonata.connect.service.support.mapper.*;

public class Converters {

    public PlayerState2CurrentPlayerStateConverter playerState2CurrentPlayerStateConverter() {
        return new PlayerState2CurrentPlayerStateConverter(
                devicesEntity2DevicesConverter(),
                playableItemEntity2PlayableItemConverter()
        );
    }

    public DevicesEntity2DevicesConverter devicesEntity2DevicesConverter() {
        return new DevicesEntity2DevicesConverter(deviceEntity2DeviceConverter());
    }

    public DeviceEntity2DeviceConverter deviceEntity2DeviceConverter() {
        return new DeviceEntity2DeviceConverter();
    }

    public PlayableItemEntity2PlayableItemConverter playableItemEntity2PlayableItemConverter() {
        return new PlayableItemEntity2PlayableItemConverter();
    }


    public CurrentPlayerState2CurrentlyPlayingPlayerStateConverter currentPlayerStateConverter() {
        return new CurrentPlayerState2CurrentlyPlayingPlayerStateConverter();
    }
}
