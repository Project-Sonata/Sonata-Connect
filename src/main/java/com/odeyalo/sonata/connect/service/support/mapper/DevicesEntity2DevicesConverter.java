package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.DevicesEntity;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.model.Devices;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convert {@link DevicesEntity} entity to {@link Devices}
 */
@Component
public class DevicesEntity2DevicesConverter implements Converter<DevicesEntity, Devices> {
    private final DeviceEntity2DeviceConverter deviceConverterSupport;

    public DevicesEntity2DevicesConverter(DeviceEntity2DeviceConverter deviceConverter) {
        this.deviceConverterSupport = deviceConverter;
    }

    @Override
    public Devices convertTo(DevicesEntity devicesEntity) {
        List<Device> deviceModels = devicesEntity.stream().map(deviceConverterSupport::convertTo).toList();
        return Devices.builder().devices(deviceModels).build();
    }
}