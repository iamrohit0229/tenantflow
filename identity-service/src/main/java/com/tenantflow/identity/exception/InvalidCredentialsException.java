package com.tenantflow.identity.exception;

public class InvalidCredentialsException extends RuntimeException {

    // Same message for unknown email and wrong password to avoid user enumeration.
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
