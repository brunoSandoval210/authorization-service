package com.autorization.autorization.auth.domain.exception;

public class PermissionAlreadyExistsException extends RuntimeException {
    public PermissionAlreadyExistsException(String message) {
        super(message);
    }
}
