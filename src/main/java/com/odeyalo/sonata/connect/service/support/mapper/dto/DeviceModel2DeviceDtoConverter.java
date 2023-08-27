package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class DeviceModel2DeviceDtoConverter implements Converter<DeviceModel, DeviceDto> {

    @Override
    public DeviceDto convertTo(DeviceModel device) {
        return DeviceDto.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .volume(device.getVolume())
                .active(device.isActive())
                .build();
    }
}
