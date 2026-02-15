package com.autorization.autorization.auth.domain.exception;

public class RoleNotAssignedException extends RuntimeException {
    public RoleNotAssignedException(String message) {
        super(message);
    }
}
