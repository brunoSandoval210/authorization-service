package com.autorization.autorization.auth.adapter.in.web.controller.rest;

import com.autorization.autorization.auth.adapter.in.web.request.CreateModuleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateModuleRequest;
import com.autorization.autorization.auth.application.dto.out.ModuleResponse;
import com.autorization.autorization.auth.domain.port.in.ModuleUseCasePort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para gestionar módulos: creación, actualización,
 * activación/desactivación y búsqueda.
 *
 * Agrupación con @Tag: permite que Swagger UI muestre los endpoints en la
 * sección "Módulos".
 */
@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Módulos", description = "Operaciones relacionadas con la gestión de módulos")
public class ModuleController {

        private final ModuleUseCasePort moduleUseCasePort;

        @Operation(summary = "Crear módulo")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Módulo creado correctamente", content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Conflicto: módulo ya existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "MODULOS", action = "CREAR_MODULO")
        @PostMapping
        public ResponseEntity<ModuleResponse> create(@Valid @RequestBody CreateModuleRequest request) {
                ModuleResponse created = moduleUseCasePort.create(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

        @Operation(summary = "Actualizar módulo")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Módulo actualizado", content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Módulo no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "MODULOS", action = "ACTUALIZAR_MODULO")
        @PutMapping("/{id}")
        public ResponseEntity<ModuleResponse> update(@PathVariable UUID id,
                        @Valid @RequestBody UpdateModuleRequest request) {
                ModuleResponse updated = moduleUseCasePort.update(id, request);
                return ResponseEntity.ok(updated);
        }

        @Operation(summary = "Desactivar módulo")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Módulo desactivado"),
                        @ApiResponse(responseCode = "404", description = "Módulo no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "MODULOS", action = "DESACTIVAR_MODULO")
        @PatchMapping("/{id}/deactivate")
        public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
                moduleUseCasePort.deactivate(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Activar módulo")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Módulo activado"),
                        @ApiResponse(responseCode = "404", description = "Módulo no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @AuditLog(module = "MODULOS", action = "ACTIVAR_MODULO")
        @PatchMapping("/{id}/activate")
        public ResponseEntity<Void> activate(@PathVariable UUID id) {
                moduleUseCasePort.activate(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Obtener módulo por id")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Módulo encontrado", content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Módulo no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<ModuleResponse> findById(@PathVariable UUID id) {
                return moduleUseCasePort.findById(id)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Listar todos los módulos")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Listado devuelto")
        })
        @GetMapping
        public ResponseEntity<List<ModuleResponse>> findAll() {
                List<ModuleResponse> list = moduleUseCasePort.findAll();
                return ResponseEntity.ok(list);
        }

        @Operation(summary = "Buscar módulos (paginado)", description = "Buscar módulos por nombre (parcial). Parámetros de paginación: page = índice de página (0-based), size = elementos por página.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Resultados de la búsqueda", content = @Content(schema = @Schema(implementation = PaginatedResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/search")
        public ResponseEntity<PaginatedResponse<ModuleResponse>> search(
                        @Parameter(description = "name: filtro por nombre (opcional)") @RequestParam(required = false) String name,
                        @Parameter(description = "page: índice de página (0-based)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "size: tamaño de página") @RequestParam(defaultValue = "10") int size) {
                PaginatedResponse<ModuleResponse> result = moduleUseCasePort.search(name, page, size);
                return ResponseEntity.ok(result);
        }
}
