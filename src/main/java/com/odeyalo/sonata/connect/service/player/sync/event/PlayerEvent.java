package com.odeyalo.sonata.connect.service.player.sync.event;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract event that can be pushed to the client to notify about update
 */
public interface PlayerEvent {
    /**
     * @return updated player state for this event
     */
    @NotNull
    CurrentPlayerState getCurrentPlayerState();

    /**
     * @return event type associated with this event
     */
    @NotNull
    EventType getEventType();

    /**
     * Return the device that targeted this event, if the event was invoked manually by Oauth2 client without including device ID, then currently active device should be used
     * @return - device id that targeted event, never null
     */
    @NotNull
    String getDeviceThatChanged();

    /**
     * List of the player event types to return to the client
     */
    enum EventType {
        PLAYER_STATE_UPDATED,
        QUEUE_STATE_CHANGED,
        NEW_DEVICE_CONNECTED,
        DEVICE_DISAPPEARED
    }
}
