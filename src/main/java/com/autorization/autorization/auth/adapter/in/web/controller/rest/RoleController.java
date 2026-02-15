package com.autorization.autorization.auth.adapter.in.web.controller.rest;

import com.autorization.autorization.auth.adapter.in.web.request.CreateRoleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateRoleRequest;
import com.autorization.autorization.auth.application.dto.out.RoleResponse;
import com.autorization.autorization.auth.domain.port.in.RoleUseCasePort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import com.autorization.autorization.shared.domain.exception.ErrorResponse;
import com.autorization.autorization.shared.annotation.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Gestión de roles y asignación de permisos")
public class RoleController {
    private final RoleUseCasePort roleUseCasePort;

    @Operation(summary = "Crear rol")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rol creado", content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto: rol ya existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @AuditLog(module = "ROLES", action = "CREAR_ROL")
    @PostMapping
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse created = roleUseCasePort.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar datos básicos del rol")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol actualizado", content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @AuditLog(module = "ROLES", action = "ACTUALIZAR_ROL")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        RoleResponse updated = roleUseCasePort.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/deactivate")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rol desactivado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @AuditLog(module = "ROLES", action = "DESACTIVAR_ROL")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        roleUseCasePort.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rol activado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @AuditLog(module = "ROLES", action = "ACTIVAR_ROL")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        roleUseCasePort.activate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol encontrado", content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoleResponse> findById(@PathVariable UUID id) {
        return roleUseCasePort.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado devuelto"),
            @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RoleResponse>> findAll() {
        List<RoleResponse> list = roleUseCasePort.findAll();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Asignar permiso a un rol", description = "Vincula un permiso existente al rol especificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Permiso vinculado"),
            @ApiResponse(responseCode = "404", description = "Rol o Permiso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El permiso ya está asignado a este rol", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @AuditLog(module = "ROLES", action = "ASIGNAR_PERMISO")
    @PostMapping("/{id}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermission(@PathVariable UUID id, @PathVariable UUID permissionId) {
        roleUseCasePort.addPermission(id, permissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remover permiso de un rol")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Permiso removido"),
            @ApiResponse(responseCode = "404", description = "Rol o Permiso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @AuditLog(module = "ROLES", action = "REMOVER_PERMISO")
    @DeleteMapping("/{id}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermission(@PathVariable UUID id, @PathVariable UUID permissionId) {
        roleUseCasePort.removePermission(id, permissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Búsqueda paginada de roles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<RoleResponse>> search(
            @Parameter(description = "Nombre del rol") @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<RoleResponse> result = roleUseCasePort.search(name, page, size);
        return ResponseEntity.ok(result);
    }
}
