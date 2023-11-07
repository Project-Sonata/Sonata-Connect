package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.model.Device;
import org.springframework.stereotype.Component;

/**
 * Convert Device to DeviceModel
 */
@Component
public class Device2DeviceEntityConverter implements Converter<Device, DeviceEntity> {

    @Override
    public DeviceEntity convertTo(Device deviceEntity) {
        return DeviceEntity.builder()
                .id(deviceEntity.getDeviceId())
                .name(deviceEntity.getDeviceName())
                .deviceType(deviceEntity.getDeviceType())
                .volume(deviceEntity.getVolume())
                .active(deviceEntity.isActive())
                .build();
    }
}