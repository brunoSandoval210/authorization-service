package com.autorization.autorization.auth.adapter.out.jpa;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Permission;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.PermissionJPAMapper;
import com.autorization.autorization.auth.adapter.out.jpa.repository.ModuleRepository;
import com.autorization.autorization.auth.adapter.out.jpa.repository.PermissionRepository;
import com.autorization.autorization.shared.domain.exception.PersistenceException;
import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;
import com.autorization.autorization.auth.domain.port.out.PermissionRepositoryPort;
import com.autorization.autorization.shared.domain.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionRepository permissionRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public PermissionDomain save(PermissionDomain domain) {
        try {
            Optional<Permission> existingPermission = permissionRepository.findById(domain.getPermissionId().id());
            Permission entityToSave;
            if (existingPermission.isPresent()) {
                entityToSave = existingPermission.get();
                entityToSave.setName(domain.getName().value());
                entityToSave.setDescription(domain.getDescription().description());
                entityToSave.setStatus(domain.getStatus());
            } else {
                entityToSave = PermissionJPAMapper.fromDomain(domain);
            }
            if (domain.getModule() != null) {
                UUID moduleId = domain.getModule().id();
                var moduleRef = moduleRepository.getReferenceById(moduleId);
                entityToSave.setModule(moduleRef);
            }
            var saved = permissionRepository.save(entityToSave);
            return PermissionJPAMapper.toDomain(saved);

        } catch (Exception e) {
            log.error("Error al persistir permiso: {}", e.getMessage(), e);
            throw new PersistenceException("Error al persistir permiso", e);
        }
    }

    @Override
    public Optional<PermissionDomain> findById(PermissionId id) {
        try {
            return permissionRepository.findById(id.id()).map(PermissionJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar permiso por id {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al buscar permiso por id", e);
        }
    }

    @Override
    public Optional<PermissionDomain> findByName(PermissionName name) {
        try {
            return permissionRepository.findByName(name.value())
                    .map(PermissionJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar permiso por nombre {}: {}", name, e.getMessage(), e);
            throw new PersistenceException("Error al buscar permiso por nombre", e);
        }
    }

    @Override
    public List<PermissionDomain> findAll() {
        try {
            return permissionRepository.findAll().stream().map(PermissionJPAMapper::toDomain).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener todos los permisos: {}", e.getMessage(), e);
            throw new PersistenceException("Error al obtener todos los permisos", e);
        }
    }

    @Override
    public void deleteById(PermissionId id) {
        try {
            permissionRepository.deleteById(id.id());
        } catch (Exception e) {
            log.error("Error al eliminar permiso por id {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al eliminar permiso por id", e);
        }
    }

    @Override
    public void updateEnabled(PermissionId id, boolean enabled) {
        try {
            var maybe = permissionRepository.findById(id.id());
            maybe.ifPresent(p -> {
                p.setStatus(enabled ? Status.ACTIVO : Status.INACTIVO);
                permissionRepository.save(p);
            });
        } catch (Exception e) {
            log.error("Error al actualizar estado del permiso {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al actualizar estado del permiso", e);
        }
    }

    @Override
    public Page<PermissionDomain> searchByName(String name, org.springframework.data.domain.Pageable pageable) {
        try {
            // normalizar nombre: trim y tratar cadenas vacías como null
            String qname = null;
            if (name != null) {
                String trimmed = name.trim();
                if (!trimmed.isBlank()) {
                    qname = trimmed;
                }
            }

            Page<Permission> pageEntities;
            if (qname == null) {
                // Si no hay filtro por nombre, usar findAll(pageable) que es más robusto.
                pageEntities = permissionRepository.findAll(pageable);
            } else {
                pageEntities = permissionRepository.searchByName(qname, pageable);
            }

            return pageEntities.map(PermissionJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar permisos por nombre='{}' (normalizado) : {} - causa: {}", name, e.getMessage(), e.getClass().getName(), e);
            throw new PersistenceException("Error al buscar permisos por nombre", e);
        }
    }
}
