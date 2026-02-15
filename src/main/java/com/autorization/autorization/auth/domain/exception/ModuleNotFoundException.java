package com.autorization.autorization.auth.domain.exception;

public class ModuleNotFoundException extends RuntimeException {
    public ModuleNotFoundException(String message) {
        super(message);
    }
}
