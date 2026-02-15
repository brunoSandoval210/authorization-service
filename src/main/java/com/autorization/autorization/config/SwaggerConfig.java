package com.autorization.autorization.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.http.HttpHeaders;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auth & Authorization Service API",
                description = """
                        API para la gestión de usuarios, roles, permisos y módulos bajo una Arquitectura Hexagonal.
                        
                        Incluye:
                        - Autenticación mediante JWT.
                        - Trazabilidad con RequestId (X-Request-Id).
                        - Auditoría de mantenimiento automatizada.
                        """,
                version = "1.0.0",
                contact = @Contact(
                        name = "Bruno Sandoval",
                        email = "bruno.sandoval210@gmail.com"
                )
        ),
        servers = {
                @Server(description = "Entorno de Desarrollo", url = "http://localhost:8080")
        },
        // El nombre aquí debe ser el mismo que en @SecurityScheme name
        security = @SecurityRequirement(name = "JWT_Auth")
)
@SecurityScheme(
        name = "JWT_Auth", // <--- Nombre unificado
        description = "Ingresa el token JWT obtenido del login (sin la palabra 'Bearer ')",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}