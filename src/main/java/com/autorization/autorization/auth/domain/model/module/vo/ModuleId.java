package com.autorization.autorization.auth.domain.model.module.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

import java.util.UUID;

public record ModuleId(UUID id) {
    public ModuleId {
        if (id == null) {
            throw new NullValueException("moduleId");
        }
    }
}
