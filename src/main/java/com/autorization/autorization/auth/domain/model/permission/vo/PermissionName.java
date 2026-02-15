package com.autorization.autorization.auth.domain.model.permission.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

public record PermissionName (String value){
    public PermissionName {
        if (value == null) {
            throw new NullValueException("permissionName");
        }
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("El nombre del permiso no puede estar vacío");
        }
    }
}
