package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.model.User;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Central interface for Sonata-Connect functionality.
 * <p>
 *     Basic Sonata-Connect workflow:
 *
 *     Requirements:
 *     - Device that already registered and is accessible through "get all devices" endpoint
 *
 *     Device appeared on Sonata-Connect form.
 *     User clicks on device. Device from which action has happened send request to
 *     Sonata-Connect, Sonata-Connect generate SCAT(Sonata-Connect Authentication Token).
 *     SCAT is being sent in local WIFI area, then target device do:
 *     - retrieve SCAT from request
 *     - send request to Sonata-Connect
 *          Sonata-Connect checks token validity and if it's valid send access token
 *     - target device got access token and now can stream music
 * </p>
 */
public interface SonataConnectManager {

    @NotNull
    Mono<SCAToken> generateSCAToken(@NotNull DeviceConnectionAuthenticationTarget deviceTarget,
                                    @NotNull User user);

    @NotNull
    Mono<AccessToken> exchangeForToken(@NotNull String scat);
}
