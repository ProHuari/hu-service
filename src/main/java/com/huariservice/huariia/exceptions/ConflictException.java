package com.huariservice.huariia.exceptions;

/**
 * Thrown when a resource already exists or would violate a uniqueness rule
 * (duplicate e-mail, repeated enrollment, position already in use...).
 * Mapped to HTTP 409 Conflict by {@link GlobalExceptionHandler}.
 */
public class ConflictException extends RuntimeException {

    /**
     * Creates a new conflict exception.
     *
     * @param message user-facing message (in Portuguese) describing the conflict
     */
    public ConflictException(String message) {
        super(message);
    }
}
