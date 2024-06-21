package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.DeviceSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convert {@link DeviceEntity} to {@link Device}
 */
@Mapper(componentModel = "spring", imports = {
        DeviceSpec.Volume.class,
        DeviceSpec.DeviceStatus.class
})
public interface DeviceEntity2DeviceConverter extends Converter<DeviceEntity, Device> {

    @Mapping(source = "id", target = "deviceId")
    @Mapping(source = "name", target = "deviceName")
    @Mapping(target = "volume", expression = "java( Volume.from(deviceEntity.getVolume()) )")
    @Mapping(target = "status", expression = "java( DeviceStatus.fromBoolean(deviceEntity.isActive()) )")
    Device convertTo(DeviceEntity deviceEntity);

}