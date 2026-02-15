package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.CreatePermissionRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdatePermissionRequest;
import com.autorization.autorization.auth.application.dto.out.PermissionResponse;
import com.autorization.autorization.auth.application.services.mapper.PermissionMapper;
import com.autorization.autorization.auth.domain.exception.PermissionAlreadyExistsException;
import com.autorization.autorization.auth.domain.exception.PermissionNotFoundException;
import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;
import com.autorization.autorization.auth.domain.port.in.PermissionUseCasePort;
import com.autorization.autorization.auth.domain.port.out.PermissionRepositoryPort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para la gestión de permisos.
 *
 * Responsabilidades:
 * - Orquestar creación/actualización de PermissionDomain via PermissionRepositoryPort.
 * - Validar unicidad y exponer activate/deactivate y búsqueda paginada.
 */
@Service
@RequiredArgsConstructor
public class PermissionService implements PermissionUseCasePort {

    private final PermissionRepositoryPort permissionRepositoryPort;

    @Override
    public PermissionResponse create(CreatePermissionRequest request) {
        PermissionDomain domain = PermissionMapper.toDomain(request);
        PermissionName name = domain.getName();
        if (name != null) {
            Optional<PermissionDomain> byName = permissionRepositoryPort.findByName(name);
            if (byName.isPresent()) {
                throw new PermissionAlreadyExistsException("El permiso ya existe");
            }
        }
        PermissionDomain saved = permissionRepositoryPort.save(domain);
        return PermissionMapper.toResponse(saved);
    }

    @Override
    public PermissionResponse update(UUID id, UpdatePermissionRequest request) {
        PermissionId pid = new PermissionId(id);
        PermissionDomain existing = permissionRepositoryPort.findById(pid)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado"));

        PermissionMapper.applyUpdate(existing, request);

        PermissionDomain saved = permissionRepositoryPort.save(existing);
        return PermissionMapper.toResponse(saved);
    }

    @Override
    public void deactivate(UUID id) {
        PermissionId pid = new PermissionId(id);
        permissionRepositoryPort.findById(pid)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado"));

        permissionRepositoryPort.updateEnabled(pid, false);
    }

    @Override
    public void activate(UUID id) {
        PermissionId pid = new PermissionId(id);
        permissionRepositoryPort.findById(pid)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado"));

        permissionRepositoryPort.updateEnabled(pid, true);
    }

    @Override
    public Optional<PermissionResponse> findById(UUID id) {
        PermissionId pid = new PermissionId(id);
        return permissionRepositoryPort.findById(pid).map(PermissionMapper::toResponse);
    }

    @Override
    public List<PermissionResponse> findAll() {
        return permissionRepositoryPort.findAll().stream().map(PermissionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse<PermissionResponse> search(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PermissionDomain> pageResult = permissionRepositoryPort.searchByName(name, pageable);

        List<PermissionDomain> content = pageResult.getContent();
        List<PermissionResponse> responses = content.stream().map(PermissionMapper::toResponse).collect(Collectors.toList());

        return new PaginatedResponse<>(
                responses,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }
}
