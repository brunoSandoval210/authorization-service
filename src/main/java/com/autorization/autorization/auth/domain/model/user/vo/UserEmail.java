package com.autorization.autorization.auth.domain.model.user.vo;

public record UserEmail(String value) {
    public UserEmail {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }
}