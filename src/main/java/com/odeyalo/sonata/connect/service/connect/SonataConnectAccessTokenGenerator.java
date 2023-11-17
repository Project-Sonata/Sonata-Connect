package com.odeyalo.sonata.connect.service.connect;

import com.odeyalo.sonata.connect.exception.SonataConnectAccessTokenGenerationException;
import com.odeyalo.sonata.connect.model.User;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Generate Sonata-Connect Access Token with specific permissions.
 * Permissions include:
 * Capability to be streaming device
 * Capability to request info about album, track, playlist, user info
 */
public interface SonataConnectAccessTokenGenerator {

    /**
     * Generate Sonata-Connect specific access token.
     * token should be valid on authorization server and can be used to access resources.
     *
     * @param user - user to generate token to
     * @return - access token or Mono with error
     * @throws SonataConnectAccessTokenGenerationException - if access token can't be generated. Any exception should be wrapped in this
     */
    @NotNull
    Mono<AccessToken> generateAccessToken(@NotNull User user);
}
