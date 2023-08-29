package com.odeyalo.sonata.connect.exception;

import lombok.Getter;
import lombok.experimental.StandardException;

/**
 * Exception that should be thrown when there is no active device found for the given user
 */
@StandardException
@Getter
public class NoActiveDeviceException extends RuntimeException implements ReasonCodeAware {
    final String reasonCode = "no_active_device";
}