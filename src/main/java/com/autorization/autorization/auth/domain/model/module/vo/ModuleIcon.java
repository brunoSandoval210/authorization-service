package com.autorization.autorization.auth.domain.model.module.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

public record ModuleIcon(String icon) {
    public ModuleIcon{
        if (icon == null) {
            throw new NullValueException("moduleIcon");
        }
        icon = icon.trim();
        if (icon.isEmpty()) {
            throw new IllegalArgumentException("El icono del modulo no puede estar vacío");
        }
    }
}
