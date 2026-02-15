package com.autorization.autorization.auth.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateModuleRequest(
        @NotBlank(message = "El nombre del módulo es obligatorio")
        @Size(max = 100, message = "El nombre del módulo no puede tener más de 100 caracteres")
        String name,

        @NotBlank(message = "El path es obligatorio")
        @Size(max = 200, message = "El path no puede tener más de 200 caracteres")
        @Pattern(regexp = "^/.*", message = "El path debe comenzar con '/'")
        String path,

        @Size(max = 100, message = "El icono no puede tener más de 100 caracteres")
        String icon
) {}
