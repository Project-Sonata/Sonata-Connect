package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convert {@link DeviceEntity} to {@link Device}
 */
@Mapper(componentModel = "spring")
public interface DeviceEntity2DeviceConverter extends Converter<DeviceEntity, Device> {

    @Mapping(source = "id", target = "deviceId")
    @Mapping(source = "name", target = "deviceName")
    Device convertTo(DeviceEntity deviceEntity);

}