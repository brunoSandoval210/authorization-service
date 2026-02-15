package com.autorization.autorization.auth.adapter.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        String name,
        String lastName,
        String secondName,
        @Email(message = "Email inválido")
        String email,
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password
) {}

