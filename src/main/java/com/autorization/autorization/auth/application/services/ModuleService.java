package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.CreateModuleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateModuleRequest;
import com.autorization.autorization.auth.application.dto.out.ModuleResponse;
import com.autorization.autorization.auth.application.services.mapper.ModuleMapper;
import com.autorization.autorization.auth.domain.exception.ModuleNotFoundException;
import com.autorization.autorization.auth.domain.model.module.ModuleDomain;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import com.autorization.autorization.auth.domain.port.in.ModuleUseCasePort;
import com.autorization.autorization.auth.domain.port.out.ModuleRepositoryPort;
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
 * Servicio de aplicación para la gestión de módulos.
 *
 * Responsabilidades:
 * - Orquestar la creación y actualización de módulos via ModuleRepositoryPort.
 * - Aplicar reglas básicas: validación de existencia, mapeo entre request y dominio, preservación de campos auditables.
 * - Proveer operaciones de activación/desactivación y búsquedas paginadas.
 */
@Service
@RequiredArgsConstructor
public class ModuleService implements ModuleUseCasePort {

    private final ModuleRepositoryPort moduleRepositoryPort;

    /**
     * Crear un nuevo módulo.
     * - Mapea CreateModuleRequest a dominio.
     * - Valida unicidad cuando corresponda (delegado al repositorio).
     * - Persiste y devuelve ModuleResponse.
     *
     * @param request datos del módulo a crear
     * @return ModuleResponse representación del módulo creado
     */
    @Override
    public ModuleResponse create(CreateModuleRequest request) {
        ModuleDomain domain = ModuleMapper.toDomain(request);
        ModuleDomain saved = moduleRepositoryPort.save(domain);
        return ModuleMapper.toResponse(saved);
    }

    /**
     * Actualizar un módulo existente (parcial).
     * - Busca el módulo, aplica cambios parciales mediante el mapper y persiste.
     *
     * @param id UUID del módulo a actualizar
     * @param request datos a modificar
     * @return ModuleResponse módulo actualizado
     * @throws ModuleNotFoundException si no existe el módulo
     */
    @Override
    public ModuleResponse update(UUID id, UpdateModuleRequest request) {
        ModuleId mid = new ModuleId(id);
        ModuleDomain existing = moduleRepositoryPort.findById(mid)
                .orElseThrow(() -> new ModuleNotFoundException("Modulo no encontrado"));

        ModuleMapper.applyUpdate(existing, request);

        ModuleDomain saved = moduleRepositoryPort.save(existing);
        return ModuleMapper.toResponse(saved);
    }

    /**
     * Desactivar un módulo (status = INACTIVO).
     *
     * @param id UUID del módulo a desactivar
     * @throws ModuleNotFoundException si no existe el módulo
     */
    @Override
    public void deactivate(UUID id) {
        ModuleId mid = new ModuleId(id);
        moduleRepositoryPort.findById(mid)
                .orElseThrow(() -> new ModuleNotFoundException("Modulo no encontrado"));

        moduleRepositoryPort.updateEnabled(mid, false);
    }

    /**
     * Activar un módulo (status = ACTIVO).
     *
     * @param id UUID del módulo a activar
     * @throws ModuleNotFoundException si no existe el módulo
     */
    @Override
    public void activate(UUID id) {
        ModuleId mid = new ModuleId(id);
        moduleRepositoryPort.findById(mid)
                .orElseThrow(() -> new ModuleNotFoundException("Modulo no encontrado"));

        moduleRepositoryPort.updateEnabled(mid, true);
    }

    /**
     * Buscar módulo por id.
     *
     * @param id UUID del módulo
     * @return Optional<ModuleResponse>
     */
    @Override
    public Optional<ModuleResponse> findById(UUID id) {
        ModuleId mid = new ModuleId(id);
        return moduleRepositoryPort.findById(mid).map(ModuleMapper::toResponse);
    }

    /**
     * Listar todos los módulos.
     *
     * @return lista de ModuleResponse
     */
    @Override
    public List<ModuleResponse> findAll() {
        return moduleRepositoryPort.findAll().stream().map(ModuleMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Buscar módulos con paginación y filtro opcional por nombre.
     *
     * @param name filtro parcial (opcional)
     * @param page índice de página (0-based)
     * @param size tamaño de página
     * @return PaginatedResponse con ModuleResponse
     */
    @Override
    public PaginatedResponse<ModuleResponse> search(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ModuleDomain> pageResult = moduleRepositoryPort.searchByName(name, pageable);

        List<ModuleDomain> content = pageResult.getContent();
        List<ModuleResponse> responses = content.stream().map(ModuleMapper::toResponse).collect(Collectors.toList());

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
