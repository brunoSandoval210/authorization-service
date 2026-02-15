package com.autorization.autorization.auth.domain.exception;

public class PermissionNotAssignedException extends RuntimeException {
    public PermissionNotAssignedException(String message) {
        super(message);
    }
}
