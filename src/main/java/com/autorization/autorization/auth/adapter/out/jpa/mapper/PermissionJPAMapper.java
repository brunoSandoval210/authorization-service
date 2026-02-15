package com.autorization.autorization.auth.adapter.out.jpa.mapper;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Module;
import com.autorization.autorization.auth.adapter.out.jpa.entity.Permission;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.exception.MappingException;
import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionDescription;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionModule;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;

public class PermissionJPAMapper {
    public static PermissionDomain toDomain(Permission entity) {
        String idStr = entity == null || entity.getPermissionId() == null ? "-" : entity.getPermissionId().toString();
        try {
            if (entity == null) return null;

            var id = new PermissionId(entity.getPermissionId());
            var name = new PermissionName(entity.getName());
            var description = entity.getDescription() == null ? null : new PermissionDescription(entity.getDescription());

            PermissionModule module = null;
            if (entity.getModule() != null) {
                // Mapeamos el VO del módulo desde la entidad JPA
                module = new PermissionModule(entity.getModule().getModuleId(), entity.getModule().getName());
            }

            return new PermissionDomain(id, name, description, module,entity.getStatus());
        } catch (Exception e) {
            throw new MappingException("Error al transformar Permission entity a domain. id=" + idStr, e);
        }
    }

    public static Permission fromDomain(PermissionDomain domain) {
        if (domain == null) return null;
        String idStr = domain.getPermissionId().id().toString();

        try {
            Permission entity = new Permission();
            entity.setPermissionId(domain.getPermissionId().id());
            entity.setName(domain.getName().value());
            entity.setDescription(domain.getDescription() == null ? null : domain.getDescription().description());

            entity.setStatus(domain.getStatus());

            if (domain.getModule() != null) {
                Module moduleEntity = new Module();
                moduleEntity.setModuleId(domain.getModule().id());
                entity.setModule(moduleEntity);
            }
            return entity;
        } catch (Exception e) {
            throw new MappingException("Error al transformar Permission domain a entity. id=" + idStr, e);
        }
    }
}