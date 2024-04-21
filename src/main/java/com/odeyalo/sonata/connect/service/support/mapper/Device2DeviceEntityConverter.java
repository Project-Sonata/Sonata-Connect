package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convert {@link Device} to {@link DeviceEntity}
 */
@Mapper(componentModel = "spring")
public interface Device2DeviceEntityConverter extends Converter<Device, DeviceEntity> {

    @Mapping(source = "deviceId", target = "id")
    @Mapping(source = "deviceName", target = "name")
    DeviceEntity convertTo(Device deviceEntity);
}