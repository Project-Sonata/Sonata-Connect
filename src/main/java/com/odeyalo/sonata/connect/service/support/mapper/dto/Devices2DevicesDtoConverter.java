package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.model.Devices;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Devices2DevicesDtoConverter implements Converter<Devices, DevicesDto> {
    private final Device2DeviceDtoConverter deviceDtoConverterSupport;

    public Devices2DevicesDtoConverter(Device2DeviceDtoConverter deviceDtoConverterSupport) {
        this.deviceDtoConverterSupport = deviceDtoConverterSupport;
    }

    @Override
    public DevicesDto convertTo(Devices devices) {
        List<DeviceDto> dtos = devices.stream().map(deviceDtoConverterSupport::convertTo).toList();
        return DevicesDto.builder().devices(dtos).build();
    }
}
