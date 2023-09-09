package com.odeyalo.sonata.connect.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface DevicesEntity extends Iterable<DeviceEntity> {

    List<DeviceEntity> getDevices();

    default List<DeviceEntity> getActiveDevices() {
        return new ArrayList<>(getDevices().stream().filter(DeviceEntity::isActive).toList());
    }

    default boolean isEmpty() {
        return getDevices().isEmpty();
    }

    default int size() {
        return getDevices().size();
    }

    default void addDevice(DeviceEntity deviceEntity) {
        getDevices().add(deviceEntity);
    }

    default DeviceEntity getDevice(int index) {
        return getDevices().get(index);
    }

    default void removeDevice(int index) {
        getDevices().remove(index);
    }

    default void removeIf(Predicate<DeviceEntity> predicate) {
        getDevices().removeIf(predicate);
    }

    default Stream<DeviceEntity> stream() {
        return getDevices().stream();
    }

    @Override
    default Iterator<DeviceEntity> iterator() {
        return getDevices().iterator();
    }
}
