package com.autorization.autorization.auth.adapter.out.jpa;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Module;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.ModuleJPAMapper;
import com.autorization.autorization.shared.domain.exception.PersistenceException;
import com.autorization.autorization.auth.adapter.out.jpa.repository.ModuleRepository;
import com.autorization.autorization.auth.domain.model.module.ModuleDomain;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import com.autorization.autorization.auth.domain.port.out.ModuleRepositoryPort;
import com.autorization.autorization.shared.domain.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModuleRepositoryAdapter implements ModuleRepositoryPort {

    private final ModuleRepository moduleRepository;

    @Override
    public ModuleDomain save(ModuleDomain domain) {
        try {
            Optional<Module> existingEntity = moduleRepository.findById(domain.getModuleId().id());

            Module entityToSave;

            if (existingEntity.isPresent()) {
                // ES UNA ACTUALIZACIÓN
                entityToSave = existingEntity.get();
                // 2. Solo actualizamos los campos que pueden cambiar (Negocio)
                // No tocamos los campos de Maintenance, JPA los respeta
                entityToSave.setName(domain.getName().name());
                entityToSave.setPath(domain.getPath().url());
                entityToSave.setIcon(domain.getIcon().icon());
            } else {
                // ES UNA CREACIÓN NUEVA
                entityToSave = ModuleJPAMapper.fromDomain(domain);
            }

            var saved = moduleRepository.save(entityToSave);
            return ModuleJPAMapper.toDomain(saved);

        } catch (Exception e) {
            log.error("Error al persistir módulo: {}", e.getMessage(), e);
            throw new PersistenceException("Error al persistir módulo", e);
        }
    }

    @Override
    public Optional<ModuleDomain> findById(ModuleId id) {
        try {
            return moduleRepository.findById(id.id()).map(ModuleJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar módulo por id {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al buscar módulo por id", e);
        }
    }

    @Override
    public List<ModuleDomain> findAll() {
        try {
            return moduleRepository.findAll().stream().map(ModuleJPAMapper::toDomain).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener todos los módulos: {}", e.getMessage(), e);
            throw new PersistenceException("Error al obtener todos los módulos", e);
        }
    }

    @Override
    public void deleteById(ModuleId id) {
        try {
            moduleRepository.deleteById(id.id());
        } catch (Exception e) {
            log.error("Error al eliminar módulo por id {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al eliminar módulo por id", e);
        }
    }

    @Override
    public void updateEnabled(ModuleId id, boolean enabled) {
        try {
            var maybe = moduleRepository.findById(id.id());
            maybe.ifPresent((Module m) -> {
                m.setStatus(enabled ? Status.ACTIVO : Status.INACTIVO);
                moduleRepository.save(m);
            });
        } catch (Exception e) {
            log.error("Error al actualizar estado del módulo {}: {}", id, e.getMessage(), e);
            throw new PersistenceException("Error al actualizar estado del módulo", e);
        }
    }

    @Override
    public Page<ModuleDomain> searchByName(String name, Pageable pageable) {
        try {
            // normalizar nombre: trim y tratar cadenas vacías como null
            String qname = null;
            if (name != null) {
                String trimmed = name.trim();
                if (!trimmed.isBlank()) {
                    qname = trimmed;
                }
            }

            Page<Module> pageEntities;
            if (qname == null) {
                // Si no hay filtro por nombre, usar findAll(pageable) que es más robusto.
                pageEntities = moduleRepository.findAll(pageable);
            } else {
                pageEntities = moduleRepository.searchByName(qname, pageable);
            }

            return pageEntities.map(ModuleJPAMapper::toDomain);
        } catch (Exception e) {
            log.error("Error al buscar módulos por nombre='{}' (normalizado) : {} - causa: {}", name, e.getMessage(), e.getClass().getName(), e);
            throw new PersistenceException("Error al buscar módulos por nombre", e);
        }
    }
}
