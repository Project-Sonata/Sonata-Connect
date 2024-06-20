package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.DeviceSpec;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {
        DeviceSpec.Volume.class
})
public interface ConnectDeviceRequest2DeviceConverter extends Converter<ConnectDeviceRequest, Device> {

    @Mapping(target = "deviceId", source = "id")
    @Mapping(target = "deviceName", source = "name")
    @Mapping(target = "active", expression = "java(true)")
    @Mapping(target = "volume", expression = "java( Volume.from( body.getVolume() ) )")
    Device convertTo(ConnectDeviceRequest body);
}
