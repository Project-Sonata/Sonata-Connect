package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.connect.model.DeviceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceDto {
    @JsonProperty("id")
    String deviceId;
    @JsonProperty("name")
    String deviceName;
    @JsonProperty("type")
    DeviceType deviceType;
    int volume;
    boolean active;
}
