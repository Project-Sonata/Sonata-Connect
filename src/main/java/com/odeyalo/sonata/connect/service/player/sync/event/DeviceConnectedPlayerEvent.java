package com.odeyalo.sonata.connect.service.player.sync.event;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import static com.odeyalo.sonata.connect.service.player.sync.event.PlayerEvent.EventType.NEW_DEVICE_CONNECTED;

/**
 * Event to invoke when new device has been connected
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class DeviceConnectedPlayerEvent implements PlayerEvent {
    CurrentPlayerState playerState;
    String deviceThatChanged;

    @NotNull
    @Override
    public CurrentPlayerState getCurrentPlayerState() {
        return playerState;
    }

    @NotNull
    @Override
    public EventType getEventType() {
        return NEW_DEVICE_CONNECTED;
    }

    @NotNull
    @Override
    public String getDeviceThatChanged() {
        return deviceThatChanged;
    }
}
