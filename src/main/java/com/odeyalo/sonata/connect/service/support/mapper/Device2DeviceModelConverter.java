package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.Device;
import com.odeyalo.sonata.connect.model.DeviceModel;
import org.springframework.stereotype.Component;

/**
 * Convert Device to DeviceModel
 */
@Component
public class Device2DeviceModelConverter implements Converter<Device, DeviceModel> {
    @Override
    public DeviceModel convertTo(Device device) {
        return DeviceModel.builder()
                .deviceId(device.getId())
                .deviceName(device.getName())
                .deviceType(device.getDeviceType())
                .volume(device.getVolume())
                .active(device.isActive())
                .build();
    }
}