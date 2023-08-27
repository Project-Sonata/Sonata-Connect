package com.odeyalo.sonata.connect.service.support.mapper.dto;

import com.odeyalo.sonata.connect.dto.ConnectDeviceRequest;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.service.support.mapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConnectDeviceRequest2DeviceModelConverter implements Converter<ConnectDeviceRequest, DeviceModel> {

    @Override
    public DeviceModel convertTo(ConnectDeviceRequest body) {
        return DeviceModel.of(body.getId(), body.getName(), body.getDeviceType(), body.getVolume(), true);
    }
}
