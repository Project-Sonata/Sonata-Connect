package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.connect.model.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

/**
 * Simple request body dto that used to connect device to the player
 */
@AllArgsConstructor(staticName = "of")
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectDeviceRequest {
    @NotBlank(message = "Id of the device is required")
    @Size(min = 16, message = "Minimum size of ID for the device is 16 characters")
    @Size(max = 16, message = "Maximum size of ID for the device is 16 characters")
    String id;
    @NotBlank(message  = "Name of the device is required")
    @Size(min = 4, message = "Minimum size of name for the device is 4 characters")
    @Size(max = 16, message = "Maximum size of name for the device is 16 characters")
    String name;
    @NotNull(message = "'device_type' json field is missing or invalid")
    @JsonProperty("device_type")
    DeviceType deviceType;
    @Range(min = 0, max = 100, message = "Volume must be between 0-100")
    byte volume;
}
