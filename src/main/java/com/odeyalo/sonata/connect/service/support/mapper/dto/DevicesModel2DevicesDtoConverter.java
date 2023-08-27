package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.model.DevicesModel;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DevicesModel2DevicesDtoConverter implements Converter<DevicesModel, DevicesDto> {
    private final DeviceModel2DeviceDtoConverter deviceDtoConverterSupport;

    public DevicesModel2DevicesDtoConverter(DeviceModel2DeviceDtoConverter deviceDtoConverterSupport) {
        this.deviceDtoConverterSupport = deviceDtoConverterSupport;
    }

    @Override
    public DevicesDto convertTo(DevicesModel devices) {
        List<DeviceDto> dtos = devices.stream().map(deviceDtoConverterSupport::convertTo).toList();
        return DevicesDto.builder().devices(dtos).build();
    }
}
