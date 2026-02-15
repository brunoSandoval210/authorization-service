package com.autorization.autorization.auth.domain.model.role.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

import java.util.UUID;

public record RoleId(UUID id) {
    public RoleId {
        if (id == null) {
            throw new NullValueException("roleId");
        }
    }
}
