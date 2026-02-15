package com.autorization.autorization.audit.adapter.in.web.controller.rest;

import com.autorization.autorization.audit.domain.model.ActivityLogDomain;
import com.autorization.autorization.audit.domain.port.in.AuditUseCasePort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import com.autorization.autorization.shared.domain.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Auditoría", description = "Endpoints para consulta de logs de actividad")
public class AuditController {

    private final AuditUseCasePort auditUseCasePort;

    @Operation(summary = "Consultar logs de actividad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de logs"),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<ActivityLogDomain>> getLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PaginatedResponse<ActivityLogDomain> response = auditUseCasePort.retrieveLogs(module, date, pageable);
        return ResponseEntity.ok(response);
    }
}
