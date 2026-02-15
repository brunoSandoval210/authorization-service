package com.autorization.autorization.auth.adapter.out.jpa.mapper;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Module;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.exception.MappingException;
import com.autorization.autorization.auth.domain.model.module.ModuleDomain;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;

public class ModuleJPAMapper {
    public static ModuleDomain toDomain(Module entity) {
        String idStr = entity == null || entity.getModuleId() == null ? "-" : entity.getModuleId().toString();
        try {
            if (entity == null) return null;
            var id = new ModuleId(entity.getModuleId());
            var name = entity.getName() == null ? null : new com.autorization.autorization.auth.domain.model.module.vo.ModuleName(entity.getName());
            var path = entity.getPath() == null ? null : new com.autorization.autorization.auth.domain.model.module.vo.ModulePath(entity.getPath());
            var icon = entity.getIcon() == null ? null : new com.autorization.autorization.auth.domain.model.module.vo.ModuleIcon(entity.getIcon());

            return new ModuleDomain(id, name, path, icon, entity.getStatus());
        } catch (Exception e) {
            throw new MappingException("Error mapping Module entity to domain. id=" + idStr, e);
        }
    }

    public static Module fromDomain(ModuleDomain domain) {
        String idStr = domain == null || domain.getModuleId() == null ? "-" : domain.getModuleId().id().toString();
        try {
            if (domain == null) return null;
            Module entity = new Module();
            entity.setModuleId(domain.getModuleId().id());
            entity.setName(domain.getName() == null ? null : domain.getName().name());
            entity.setPath(domain.getPath() == null ? null : domain.getPath().url());
            entity.setIcon(domain.getIcon() == null ? null : domain.getIcon().icon());
            entity.setStatus(domain.getStatus());
            return entity;
        } catch (Exception e) {
            throw new MappingException("Error mapping Module domain to entity. id=" + idStr, e);
        }
    }
}
