package com.autorization.autorization.auth.domain.exception;

public class PermissionAlreadyAssignedException extends RuntimeException {
    public PermissionAlreadyAssignedException(String message) {
        super(message);
    }
}
