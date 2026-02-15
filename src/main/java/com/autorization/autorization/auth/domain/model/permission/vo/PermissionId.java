package com.autorization.autorization.auth.domain.model.permission.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

import java.util.UUID;

public record PermissionId(UUID id) {
    public PermissionId {
        if (id == null) {
            throw new NullValueException("permissionId");
        }
    }
}
