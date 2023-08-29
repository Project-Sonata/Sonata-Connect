package com.odeyalo.sonata.connect.exception;

import com.odeyalo.sonata.common.context.MalformedContextUriException;

/**
 * MalformedContextUriException extension that also implement ReasonCodeAware interface
 *
 * @see MalformedContextUriException
 * @see ReasonCodeAware
 */
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
