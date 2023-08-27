package com.odeyalo.sonata.connect.service.support.mapper;

import com.odeyalo.sonata.connect.entity.Devices;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.DevicesModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convert Devices entity to DevicesModel
 */
@Component
public class DevicesToDevicesModelConverter implements Converter<Devices, DevicesModel> {
    private final Device2DeviceModelConverter deviceConverterSupport;

    public DevicesToDevicesModelConverter(Device2DeviceModelConverter deviceConverter) {
        this.deviceConverterSupport = deviceConverter;
    }

    @Override
    public DevicesModel convertTo(Devices devices) {
        List<DeviceModel> deviceModels = devices.stream().map(deviceConverterSupport::convertTo).toList();
        return DevicesModel.builder().devices(deviceModels).build();
    }
}