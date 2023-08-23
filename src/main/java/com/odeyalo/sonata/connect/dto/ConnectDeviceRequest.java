package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Simple request body dto that used to connect device to the player
 */
@AllArgsConstructor(staticName = "of")
@Builder
@Data
public class ConnectDeviceRequest {
    String id;
    String name;
    @JsonProperty("device_type")
    DeviceType deviceType;
    byte volume;
}
