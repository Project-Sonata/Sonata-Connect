package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.DeviceDto;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface Device2DeviceDtoConverter extends Converter<Device, DeviceDto> {

    @Mapping(target = "volume", expression = "java( device.getVolume().asInt() )")
    DeviceDto convertTo(Device device);

}
