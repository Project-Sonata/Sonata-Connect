package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.model.Device;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConnectDeviceRequest2DeviceConverter implements Converter<ConnectDeviceRequest, Device> {

    @Override
    public Device convertTo(ConnectDeviceRequest body) {
        return Device.of(body.getId(), body.getName(), body.getDeviceType(), body.getVolume(), true);
    }
}
