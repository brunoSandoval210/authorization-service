package com.autorization.autorization.auth.application.dto.out;

import java.util.List;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        List<PermissionResponse> permissions,
        String status
) {}