package com.autorization.autorization.auth.domain.model.permission.vo;

import java.util.UUID;

public record PermissionModule(UUID id, String name) {
    public PermissionModule {
        if (name != null) {
            String n = name.trim();
            if (n.isEmpty()) {
                name = null;
            } else {
                name = n;
            }
        }
    }
}
