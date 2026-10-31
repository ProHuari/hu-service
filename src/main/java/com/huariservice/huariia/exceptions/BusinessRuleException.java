package com.huariservice.huariia.exceptions;

/**
 * Thrown when a request is well-formed but violates a business rule
 * (e.g. completing a lesson without being enrolled in its course).
 * Mapped to HTTP 422 Unprocessable Entity by {@link GlobalExceptionHandler}.
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * Creates a new business rule exception.
     *
     * @param message user-facing message (in Portuguese) describing the violated rule
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}
