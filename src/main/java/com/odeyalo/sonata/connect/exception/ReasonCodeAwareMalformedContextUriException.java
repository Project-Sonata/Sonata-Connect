package com.odeyalo.sonata.connect.exception;

import com.odeyalo.sonata.common.context.MalformedContextUriException;
import lombok.ToString;

/**
 * MalformedContextUriException extension that also implements ReasonCodeAware interface
 *
 * @see MalformedContextUriException
 * @see ReasonCodeAware
 */
@ToString
public class ReasonCodeAwareMalformedContextUriException extends MalformedContextUriException implements ReasonCodeAware {
    public ReasonCodeAwareMalformedContextUriException(String uriString) {
        super(uriString);
    }

    public ReasonCodeAwareMalformedContextUriException(String message, String uriString) {
        super(message, uriString);
    }

    public ReasonCodeAwareMalformedContextUriException(String message, Throwable cause, String uriString) {
        super(message, cause, uriString);
    }

    public ReasonCodeAwareMalformedContextUriException(Throwable cause, String uriString) {
        super(cause, uriString);
    }

    public ReasonCodeAwareMalformedContextUriException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String uriString) {
        super(message, cause, enableSuppression, writableStackTrace, uriString);
    }

    @Override
    public String getReasonCode() {
        return "malformed_context_uri";
    }
}
