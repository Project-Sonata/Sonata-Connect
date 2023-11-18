package com.odeyalo.sonata.connect.service.connect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * Contain info about access token that has been generated
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class AccessToken {
    String tokenValue;
}
