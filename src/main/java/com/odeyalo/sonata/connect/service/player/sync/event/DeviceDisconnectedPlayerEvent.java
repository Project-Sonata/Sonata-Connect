package com.odeyalo.sonata.connect.service.player.sync.event;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class DeviceDisconnectedPlayerEvent implements PlayerEvent {
    @NotNull
    CurrentPlayerState playerState;
    @NotNull
    String deviceThatChanged;

    @NotNull
    @Override
    public CurrentPlayerState getCurrentPlayerState() {
        return playerState;
    }

    @NotNull
    @Override
    public EventType getEventType() {
        return EventType.DEVICE_DISAPPEARED;
    }

    @NotNull
    @Override
    public String getDeviceThatChanged() {
        return deviceThatChanged;
    }
}
