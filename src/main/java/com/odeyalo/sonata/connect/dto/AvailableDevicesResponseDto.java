package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableDevicesResponseDto {
    @JsonUnwrapped
    DevicesDto devices;

    public int getSize() {
        return devices.size();
    }
}
