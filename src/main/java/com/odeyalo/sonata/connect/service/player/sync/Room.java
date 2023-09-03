package com.odeyalo.sonata.connect.service.player.sync;

/**
 * Interface to represent room.
 * Note that this interface does not provide info about room members, but just return event publisher for this room
 */
public interface Room {

    RoomEventPublisher getPublisher();

}
