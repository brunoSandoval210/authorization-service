package com.autorization.autorization.auth.application.dto.out;

import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String name,
        String description,
        String status
) {}
