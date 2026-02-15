package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.CreateRoleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateRoleRequest;
import com.autorization.autorization.auth.application.dto.out.RoleResponse;
import com.autorization.autorization.auth.application.services.mapper.RoleMapper;
import com.autorization.autorization.auth.domain.exception.PermissionNotFoundException;
import com.autorization.autorization.auth.domain.exception.RoleAlreadyExistsException;
import com.autorization.autorization.auth.domain.exception.RoleNotFoundException;
import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.model.role.vo.RoleName;
import com.autorization.autorization.auth.domain.port.in.RoleUseCasePort;
import com.autorization.autorization.auth.domain.port.out.PermissionRepositoryPort;
import com.autorization.autorization.auth.domain.port.out.RoleRepositoryPort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para la gestión de roles.
 *
 * Responsabilidades:
 * - Orquestar la creación/actualización de RoleDomain mediante RoleRepositoryPort.
 * - Validar unicidad de nombre y exponer operaciones activate/deactivate y búsqueda.
 */
@Service
@RequiredArgsConstructor
public class RoleService implements RoleUseCasePort {

    private final RoleRepositoryPort roleRepositoryPort;
    private final PermissionRepositoryPort permissionRepositoryPort;

    /**
     * Crear un nuevo rol.
     * @param request CreateRoleRequest
     * @return RoleResponse creado
     * @throws RoleAlreadyExistsException si el nombre ya existe
     */
    @Override
    public RoleResponse create(CreateRoleRequest request) {
        RoleDomain domain = RoleMapper.toDomain(request);
        RoleName name = domain.getName();
        if (name != null) {
            Optional<RoleDomain> byName = roleRepositoryPort.findByName(name);
            if (byName.isPresent()) {
                throw new RoleAlreadyExistsException("El rol ya existe");
            }
        }
        RoleDomain saved = roleRepositoryPort.save(domain);
        return RoleMapper.toResponse(saved);
    }

    /**
     * Actualizar rol existente (parcial).
     */
    @Override
    public RoleResponse update(UUID id, UpdateRoleRequest request) {
        RoleId rid = new RoleId(id);
        RoleDomain existing = roleRepositoryPort.findById(rid)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

        RoleMapper.applyUpdate(existing, request);

        RoleDomain saved = roleRepositoryPort.save(existing);
        return RoleMapper.toResponse(saved);
    }

    /**
     * Desactivar rol.
     */
    @Override
    public void deactivate(UUID id) {
        RoleId rid = new RoleId(id);
        roleRepositoryPort.findById(rid)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
        roleRepositoryPort.updateEnabled(rid, false);
    }

    /**
     * Activar rol.
     */
    @Override
    public void activate(UUID id) {
        RoleId rid = new RoleId(id);
        roleRepositoryPort.findById(rid)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
        roleRepositoryPort.updateEnabled(rid, true);
    }

    @Override
    public Optional<RoleResponse> findById(UUID id) {
        RoleId rid = new RoleId(id);
        return roleRepositoryPort.findById(rid).map(RoleMapper::toResponse);
    }

    @Override
    public List<RoleResponse> findAll() {
        return roleRepositoryPort.findAll().stream().map(RoleMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void addPermission(UUID roleId, UUID permissionId) {
        RoleId rid = new RoleId(roleId);
        PermissionId pid = new PermissionId(permissionId);

        RoleDomain role = roleRepositoryPort.findById(rid)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
        PermissionDomain permission = permissionRepositoryPort.findById(pid)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado"));

        role.addPermission(permission);
        roleRepositoryPort.save(role);
    }

    @Override
    public void removePermission(UUID roleId, UUID permissionId) {
        RoleId rid = new RoleId(roleId);
        PermissionId pid = new PermissionId(permissionId);

        RoleDomain role = roleRepositoryPort.findById(rid)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
        PermissionDomain permission = permissionRepositoryPort.findById(pid)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado"));

        role.removePermission(permission);
        roleRepositoryPort.save(role);
    }

    @Override
    public PaginatedResponse<RoleResponse> search(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleDomain> pageResult = roleRepositoryPort.searchByName(name, pageable);

        List<RoleDomain> content = pageResult.getContent();
        List<RoleResponse> responses = content.stream().map(RoleMapper::toResponse).collect(Collectors.toList());

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
