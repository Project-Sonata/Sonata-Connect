package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.AvailableDevicesResponseDto;
import com.odeyalo.sonata.connect.dto.DevicesDto;
import com.odeyalo.sonata.connect.model.Devices;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {
        Devices2DevicesDtoConverter.class
})
public abstract class AvailableDevicesResponseDtoConverter {
    @Autowired
    Devices2DevicesDtoConverter devices2DevicesDtoConverter;

    public AvailableDevicesResponseDto convertTo(Devices devices) {
        final DevicesDto devicesDto = devices2DevicesDtoConverter.convertTo(devices);
        return AvailableDevicesResponseDto.of(devicesDto);
    }
}
