package com.odeyalo.sonata.connect.service.player.sync;

/**
 * Room impl that uses SinkRoomEventPublisher
 */
public class SinkRoom implements Room {
    private final RoomEventPublisher roomEventPublisher = new SinkRoomEventPublisher();

    @Override
    public RoomEventPublisher getPublisher() {
        return roomEventPublisher;
    }
}
