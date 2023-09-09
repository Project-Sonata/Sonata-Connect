package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class Device2DeviceDtoConverter implements Converter<Device, DeviceDto> {

    @Override
    public DeviceDto convertTo(Device device) {
        return DeviceDto.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .volume(device.getVolume())
                .active(device.isActive())
                .build();
    }
}
