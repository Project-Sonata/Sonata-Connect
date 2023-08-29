package com.odeyalo.sonata.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * Extension for ExceptionMessage that has reasonCode field to return
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReasonCodeAwareExceptionMessage extends ExceptionMessage {
    @JsonProperty("reason_code")
    String reasonCode;

    public ReasonCodeAwareExceptionMessage(String reasonCode, String description) {
        this.reasonCode = reasonCode;
        setDescription(description);
    }

    public static ReasonCodeAwareExceptionMessage of(String code, String description) {
        return new ReasonCodeAwareExceptionMessage(code, description);
    }
}