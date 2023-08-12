package com.odeyalo.sonata.connect.entity;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public interface Devices extends Iterable<Device> {

    List<Device> getDevices();

    default boolean isEmpty() {
        return getDevices().isEmpty();
    }

    default int size() {
        return getDevices().size();
    }

    default void addDevice(Device device) {
        getDevices().add(device);
    }

    default Device getDevice(int index) {
        return getDevices().get(index);
    }

    default Stream<Device> stream() {
        return getDevices().stream();
    }

    @Override
    default Iterator<Device> iterator() {
        return getDevices().iterator();
    }
}
