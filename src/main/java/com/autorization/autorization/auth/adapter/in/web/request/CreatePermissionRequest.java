package com.autorization.autorization.auth.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
        @NotBlank(message = "El nombre del permiso es obligatorio")
        @Size(max = 100, message = "El nombre del permiso no puede exceder 100 caracteres")
        String name,

        @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
        String description
) {}
