package com.odeyalo.sonata.connect.entity;

import com.odeyalo.sonata.connect.model.DeviceType;

public interface DeviceEntity {

    String getId();

    String getName();

    DeviceType getDeviceType();

    boolean isActive();

    int getVolume();
}
