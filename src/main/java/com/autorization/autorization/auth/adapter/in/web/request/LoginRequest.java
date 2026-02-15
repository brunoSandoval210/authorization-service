package com.autorization.autorization.auth.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Schema(example = "admin@empresa.com", description = "Correo electrónico del usuario")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email,

        @Schema(example = "password123", description = "Contraseña de acceso")
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}