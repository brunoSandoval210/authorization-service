package com.autorization.autorization.auth.application.dto.out;

import java.util.UUID;

public record ModuleResponse(
        UUID id,
        String name,
        String path,
        String icon,
        String status
) {}
