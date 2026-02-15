package com.autorization.autorization.shared.domain.exception;

public class NullValueException extends RuntimeException {

    public NullValueException(String value) {
        super(String.format("El valor '%s' no puede ser nulo", value));
    }

}