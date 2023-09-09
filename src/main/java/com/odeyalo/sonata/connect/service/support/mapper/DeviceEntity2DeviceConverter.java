package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.model.Device;
import org.springframework.stereotype.Component;

/**
 * Convert Device to DeviceModel
 */
@Component
public class DeviceEntity2DeviceConverter implements Converter<DeviceEntity, Device> {

    @Override
    public Device convertTo(DeviceEntity deviceEntity) {
        return Device.builder()
                .deviceId(deviceEntity.getId())
                .deviceName(deviceEntity.getName())
                .deviceType(deviceEntity.getDeviceType())
                .volume(deviceEntity.getVolume())
                .active(deviceEntity.isActive())
                .build();
    }
}