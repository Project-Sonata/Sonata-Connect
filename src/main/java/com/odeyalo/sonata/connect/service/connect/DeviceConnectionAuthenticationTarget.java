package com.odeyalo.sonata.connect.service.connect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * Target device for Sonata-Connect authentication
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class DeviceConnectionAuthenticationTarget {
    // Already connected device ID that already appeared on user-screen
    // (or now accessible through "get all devices" endpoint
    String id;
}
