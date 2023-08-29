package com.odeyalo.sonata.connect.exception;

/**
 * Interface that MUST be implemented by exceptions that have their reason code
 */
public interface ReasonCodeAware {
    /**
     * @return reason code associated with this exception. never null
     */
    String getReasonCode();
}