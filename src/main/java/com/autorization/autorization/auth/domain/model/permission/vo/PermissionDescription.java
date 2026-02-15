package com.autorization.autorization.auth.domain.model.permission.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

public record PermissionDescription(String description) {
    public PermissionDescription {
        if (description == null) {
            throw new NullValueException("permissionDescription");
        }
        String d = description.trim();
        if (d.isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        if (d.length() > 200) {
            throw new IllegalArgumentException("La descripción no puede superar 200 caracteres");
        }
        description = d;
    }
}
