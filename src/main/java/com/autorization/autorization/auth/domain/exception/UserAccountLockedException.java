package com.autorization.autorization.auth.domain.exception;

public class UserAccountLockedException extends RuntimeException {
    public UserAccountLockedException(String message) {
        super(message);
    }
}
