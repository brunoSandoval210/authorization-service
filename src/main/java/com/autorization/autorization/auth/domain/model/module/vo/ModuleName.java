package com.autorization.autorization.auth.domain.model.module.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

public record ModuleName(String name) {
    public ModuleName {
        if (name == null) {
            throw new NullValueException("moduleName");
        }
        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("El nombre del modulo no puede estar vacío");
        }
    }
}
