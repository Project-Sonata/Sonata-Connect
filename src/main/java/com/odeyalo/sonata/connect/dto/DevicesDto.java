package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DevicesDto {
    @JsonProperty("devices")
    List<DeviceDto> devices;

    @JsonIgnore
    public boolean isEmpty() {
        return devices.isEmpty();
    }

    public int size() {
        return devices.size();
    }

    public DeviceDto get(int index) {
        return devices.get(index);
    }
}
