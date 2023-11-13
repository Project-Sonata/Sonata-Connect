package com.odeyalo.sonata.connect.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Contain body after successful device pre-registration(authentication) of the device.
 * <p>
 * Contains SCAT to exchange it for real token
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SonataConnectAuthenticationTokenResponseDto {
    String token;
}
