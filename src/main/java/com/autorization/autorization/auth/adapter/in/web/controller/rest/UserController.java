package com.autorization.autorization.auth.adapter.in.web.controller.rest;

import com.autorization.autorization.auth.adapter.in.web.request.CreateUserRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateUserRequest;
import com.autorization.autorization.auth.application.dto.out.UserResponse;
import com.autorization.autorization.auth.domain.port.in.UserUseCasePort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import com.autorization.autorization.shared.domain.model.Status;
import com.autorization.autorization.shared.domain.exception.ErrorResponse;
import com.autorization.autorization.shared.annotation.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gestionar usuarios: creación, actualización,
 * activación/desactivación,
 * asignación/remoción de roles y búsquedas con paginación.
 *
 * Agrupación con @Tag: permite que Swagger UI muestre los endpoints en la
 * sección "Usuarios".
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
public class UserController {

        private final UserUseCasePort userUseCasePort;

        @PreAuthorize("hasAuthority('WRITE_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Crear usuario")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Usuario creado correctamente", content = @Content(schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Conflicto: usuario ya existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "USUARIOS", action = "CREAR_USUARIO")
        @PostMapping
        public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
                UserResponse created = userUseCasePort.create(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

        @PreAuthorize("hasAuthority('WRITE_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Actualizar usuario")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Usuario actualizado", content = @Content(schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "USUARIOS", action = "ACTUALIZAR_USUARIO")
        @PutMapping("/{id}")
        public ResponseEntity<UserResponse> update(@PathVariable UUID id,
                        @Valid @RequestBody UpdateUserRequest request) {
                UserResponse updated = userUseCasePort.update(id, request);
                return ResponseEntity.ok(updated);
        }

        @PreAuthorize("hasAuthority('WRITE_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Desactivar usuario")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Usuario desactivado"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "USUARIOS", action = "DESACTIVAR_USUARIO")
        @PatchMapping("/{id}/deactivate")
        public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
                userUseCasePort.deactivate(id);
                return ResponseEntity.noContent().build();
        }

        @PreAuthorize("hasAuthority('WRITE_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Activar usuario")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Usuario activado"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "USUARIOS", action = "ACTIVAR_USUARIO")
        @PatchMapping("/{id}/activate")
        public ResponseEntity<Void> activate(@PathVariable UUID id) {
                userUseCasePort.activate(id);
                return ResponseEntity.noContent().build();
        }

        @PreAuthorize("hasAuthority('READ_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Obtener usuario por id")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
                return userUseCasePort.findById(id)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @PreAuthorize("hasAuthority('READ_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Listar todos los usuarios")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Listado devuelto")
        })
        @GetMapping
        public ResponseEntity<List<UserResponse>> findAll() {
                List<UserResponse> list = userUseCasePort.findAll();
                return ResponseEntity.ok(list);
        }

        @PreAuthorize("hasAuthority('WRITE_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Asignar rol a usuario")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Rol asignado"),
                        @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "USUARIOS", action = "ASIGNAR_ROL")
        @PostMapping("/{id}/roles/{roleId}")
        public ResponseEntity<Void> assignRole(@PathVariable UUID id, @PathVariable UUID roleId) {
                userUseCasePort.assignRole(id, roleId);
                return ResponseEntity.noContent().build();
        }

        @PreAuthorize("hasAuthority('WRITE_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Revocar rol de usuario")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Rol revocado"),
                        @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "USUARIOS", action = "REVOCAR_ROL")
        @DeleteMapping("/{id}/roles/{roleId}")
        public ResponseEntity<Void> revokeRole(@PathVariable UUID id, @PathVariable UUID roleId) {
                userUseCasePort.revokeRole(id, roleId);
                return ResponseEntity.noContent().build();
        }

        @PreAuthorize("hasAuthority('READ_PRIVILEGES') or hasRole('ADMIN')")
        @Operation(summary = "Buscar usuarios (paginado)", description = "Buscar usuarios por email (parcial) y/o status. Parámetros de paginación: page = índice de página (0-based), size = elementos por página.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Resultados de la búsqueda", content = @Content(schema = @Schema(implementation = PaginatedResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/search")
        public ResponseEntity<PaginatedResponse<UserResponse>> search(
                        @Parameter(description = "email: filtro por email o userId (opcional)") @RequestParam(required = false) String email,
                        @Parameter(description = "status: ACTIVO | INACTIVO (opcional)") @RequestParam(required = false) String status,
                        @Parameter(description = "page: índice de página (0-based)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "size: tamaño de página") @RequestParam(defaultValue = "10") int size) {
                Status s = null;
                if (status != null && !status.isBlank()) {
                        try {
                                s = Status.valueOf(status.toUpperCase());
                        } catch (IllegalArgumentException ex) {
                                return ResponseEntity.badRequest().build();
                        }
                }

                PaginatedResponse<UserResponse> result = userUseCasePort.search(email, s, page, size);
                return ResponseEntity.ok(result);
        }
}