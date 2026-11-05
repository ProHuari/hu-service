package com.huariservice.huariia.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid e-mail or password");
    }
}

