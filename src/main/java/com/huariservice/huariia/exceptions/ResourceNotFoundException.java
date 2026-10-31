package com.huariservice.huariia.exceptions;

/**
 * Thrown when a requested resource (from a path id or an id sent in the body)
 * does not exist.
 * Mapped to HTTP 404 Not Found by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new resource-not-found exception.
     *
     * @param message user-facing message (in Portuguese) describing the missing
     *                resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
