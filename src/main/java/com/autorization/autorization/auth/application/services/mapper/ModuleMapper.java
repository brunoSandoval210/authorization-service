package com.autorization.autorization.auth.application.services.mapper;

import com.autorization.autorization.auth.adapter.in.web.request.CreateModuleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateModuleRequest;
import com.autorization.autorization.auth.application.dto.out.ModuleResponse;
import com.autorization.autorization.auth.domain.model.module.ModuleDomain;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleIcon;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleName;
import com.autorization.autorization.auth.domain.model.module.vo.ModulePath;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.exception.MappingException;
import com.autorization.autorization.shared.domain.model.Status;

import java.util.UUID;

public class ModuleMapper {
    public static ModuleDomain toDomain(CreateModuleRequest req) {
        try {
            return new ModuleDomain(
                    new ModuleId(UUID.randomUUID()),
                    req.name() == null ? null : new ModuleName(req.name()),
                    req.path() == null ? null : new ModulePath(req.path()),
                    req.icon() == null ? null : new ModuleIcon(req.icon()),
                    Status.ACTIVO
            );
        } catch (Exception e) {
            throw new MappingException("Error mapeando CreateModuleRequest a ModuleDomain", e);
        }
    }

    public static void applyUpdate(ModuleDomain existing, UpdateModuleRequest req) {
        try {
            if (existing == null || req == null) return;
            if (req.name() != null) existing.updateName(new ModuleName(req.name()));
            if (req.path() != null) existing.updatePath(new ModulePath(req.path()));
            if (req.icon() != null) existing.updateIcon(new ModuleIcon(req.icon()));
        } catch (Exception e) {
            throw new MappingException("Error aplicando UpdateModuleRequest a ModuleDomain", e);
        }
    }

    public static ModuleResponse toResponse(ModuleDomain domain) {
        try {
            if (domain == null) return null;
            String name = domain.getName() == null ? null : domain.getName().name();
            String path = domain.getPath() == null ? null : domain.getPath().url();
            String icon = domain.getIcon() == null ? null : domain.getIcon().icon();
            String status = domain.getStatus() == null ? null : domain.getStatus().name();

            return new ModuleResponse(domain.getModuleId().id(), name, path, icon, status);
        } catch (Exception e) {
            throw new MappingException("Error mapeando ModuleDomain a ModuleResponse", e);
        }
    }
}
