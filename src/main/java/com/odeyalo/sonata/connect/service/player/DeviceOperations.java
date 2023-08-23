package com.odeyalo.sonata.connect.service.player;

import com.odeyalo.sonata.connect.model.CurrentPlayerState;
import com.odeyalo.sonata.connect.model.DeviceModel;
import com.odeyalo.sonata.connect.model.User;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Interface to handle basic operations for connected device
 */
public interface DeviceOperations {
    
    Mono<CurrentPlayerState> addDevice(User user, DeviceModel device);

    Mono<Boolean> containsById(User user, String deviceId);

    Mono<List<DeviceModel>> getConnectedDevices(User user);

}
