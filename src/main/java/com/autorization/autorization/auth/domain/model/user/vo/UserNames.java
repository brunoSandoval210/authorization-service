package com.autorization.autorization.auth.domain.model.user.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

public record UserNames(
        String name,
        String lastName,
        String secondName) {
    public UserNames{
        // Validar y normalizar name
        if (name == null) {
            throw new NullValueException("name");
        }
        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        // Validar y normalizar lastName
        if (lastName == null) {
            throw new NullValueException("lastName");
        }
        lastName = lastName.trim();
        if (lastName.isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío");
        }

        // Normalizar secondName: permitir null, pero convertir cadenas vacías a null
        if (secondName != null) {
            secondName = secondName.trim();
            if (secondName.isEmpty()) {
                secondName = null;
            }
        }
    }
}
