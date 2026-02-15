package com.autorization.autorization.auth.domain.model.user.vo;

import com.autorization.autorization.shared.domain.exception.NullValueException;

import java.util.UUID;

public record UserId (UUID id) {
    public UserId{
        if (id == null) {
            throw new NullValueException("del user id");
        }
    }
}
