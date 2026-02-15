package com.autorization.autorization.auth.adapter.out.jpa;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Role;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.RoleJPAMapper;
import com.autorization.autorization.shared.domain.exception.PersistenceException;
import com.autorization.autorization.auth.adapter.out.jpa.repository.PermissionRepository;
import com.autorization.autorization.auth.adapter.out.jpa.repository.RoleRepository;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.port.out.RoleRepositoryPort;
import com.autorization.autorization.shared.domain.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public RoleDomain save(RoleDomain domain) {
        try {
            Optional<Role> existing = roleRepository.findById(domain.getRoleId().id());
            Role entity;
            if (existing.isPresent()) {
                entity = existing.get();
                entity.setName(domain.getName().value());
                entity.setDescription(domain.getDescription() == null ? null : domain.getDescription().description());
                entity.setStatus(domain.getStatus());

                if (domain.getPermissions() != null) {
                    var perms = domain.getPermissions().stream()
                            .map(p -> permissionRepository.getReferenceById(p.getPermissionId().id()))
                            .collect(Collectors.toSet());

                    entity.getPermissions().clear();
                    entity.getPermissions().addAll(perms);
                }
            } else {
                entity = RoleJPAMapper.fromDomain(domain);
            }

            var saved = roleRepository.save(entity);
            return RoleJPAMapper.toDomain(saved);
        } catch (Exception e) {
            log.error("Error al persistir rol: {}", e.getMessage(), e);
            throw new PersistenceException("Error al persistir rol", e);
        }
    }

    @Override
    public Optional<RoleDomain> findById(RoleId id) {
        try {
            return roleRepository.findById(id.id()).map(RoleJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar rol por id {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al buscar rol por id", e);
        }
    }

    @Override
    public Optional<RoleDomain> findByName(com.autorization.autorization.auth.domain.model.role.vo.RoleName name) {
        try {
            return roleRepository.findByName(name.value()).map(RoleJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar rol por nombre {}: {}", name, e.getMessage(), e);
            throw new PersistenceException("Error al buscar rol por nombre", e);
        }
    }

    @Override
    public List<RoleDomain> findAll() {
        try {
            return roleRepository.findAll().stream().map(RoleJPAMapper::toDomain).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener todos los roles: {}", e.getMessage(), e);
            throw new PersistenceException("Error al obtener todos los roles", e);
        }
    }

    @Override
    public boolean existsById(RoleId id) {
        try {
            return roleRepository.existsById(id.id());
        } catch (Exception e) {
            log.error("Error al verificar existencia de rol {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al verificar existencia de rol", e);
        }
    }

    @Override
    public void deleteById(RoleId id) {
        try {
            roleRepository.deleteById(id.id());
        } catch (Exception e) {
            log.error("Error al eliminar rol por id {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al eliminar rol por id", e);
        }
    }

    @Override
    public void updateEnabled(RoleId id, boolean enabled) {
        try {
            var maybe = roleRepository.findById(id.id());
            maybe.ifPresent(r -> {
                r.setStatus(enabled ? Status.ACTIVO : Status.INACTIVO);
                roleRepository.save(r);
            });
        } catch (Exception e) {
            log.error("Error al actualizar estado del rol {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al actualizar estado del rol", e);
        }
    }

    @Override
    public Page<RoleDomain> searchByName(String name, Pageable pageable) {
        try {
            // normalizar nombre: trim y tratar cadenas vacías como null
            String qname = null;
            if (name != null) {
                String trimmed = name.trim();
                if (!trimmed.isBlank()) qname = trimmed;
            }

            Page<Role> pageEntities;
            if (qname == null) {
                // Sin filtro, usar findAll(pageable) para mayor robustez
                pageEntities = roleRepository.findAll(pageable);
            } else {
                pageEntities = roleRepository.searchByName(qname, pageable);
            }

            return pageEntities.map(RoleJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar roles por nombre='{}' (normalizado) : {} - causa: {}", name, e.getMessage(), e.getClass().getName(), e);
            throw new PersistenceException("Error al buscar roles por nombre", e);
        }
    }

}
