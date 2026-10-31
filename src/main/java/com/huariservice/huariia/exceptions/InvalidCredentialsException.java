package com.huariservice.huariia.exceptions;

/**
 * Thrown when the login e-mail or password is wrong.
 * Mapped to HTTP 401 Unauthorized by {@link GlobalExceptionHandler}.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Creates a new invalid credentials exception with a generic message,
     * so that callers cannot tell whether the e-mail or the password was wrong.
     */
    public InvalidCredentialsException() {
        super("E-mail ou senha inválidos");
    }
}
