package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Contain request body to exchange SCAT to Access Token
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SCATokenExchangeRequestDto {
    @JsonProperty("scat")
    String scaToken;
}
