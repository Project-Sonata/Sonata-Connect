package com.odeyalo.sonata.connect.exception;

/**
 * Base exception class for invalid target devices
 */
public class InvalidTargetDevicesException extends RuntimeException implements ReasonCodeAware {
    String reasonCode;

    public InvalidTargetDevicesException(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public InvalidTargetDevicesException(String message, String reasonCode) {
        super(message);
        this.reasonCode = reasonCode;
    }

    public InvalidTargetDevicesException(String message, Throwable cause, String reasonCode) {
        super(message, cause);
        this.reasonCode = reasonCode;
    }

    public InvalidTargetDevicesException(Throwable cause, String reasonCode) {
        super(cause);
        this.reasonCode = reasonCode;
    }

    public InvalidTargetDevicesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String reasonCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reasonCode = reasonCode;
    }

    @Override
    public String getReasonCode() {
        return reasonCode;
    }
}
