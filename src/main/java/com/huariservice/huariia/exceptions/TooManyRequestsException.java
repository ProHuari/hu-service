package com.huariservice.huariia.exceptions;

/**
 * Thrown when a client exceeds the maximum number of allowed authentication attempts.
 */
public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}

