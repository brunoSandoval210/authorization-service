package com.autorization.autorization.auth.application.services.mapper;

import com.autorization.autorization.auth.adapter.in.web.request.CreatePermissionRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdatePermissionRequest;
import com.autorization.autorization.auth.application.dto.out.PermissionResponse;
import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionDescription;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.exception.MappingException;
import com.autorization.autorization.shared.domain.model.Status;

import java.util.UUID;

public class PermissionMapper {
    public static PermissionDomain toDomain(CreatePermissionRequest req) {
        try {
            return new PermissionDomain(
                    new PermissionId(UUID.randomUUID()),
                    req.name() == null ? null : new PermissionName(req.name()),
                    req.description() == null ? null : new PermissionDescription(req.description()),
                    null,
                    Status.ACTIVO
            );
        } catch (Exception e) {
            throw new MappingException("Error mapeando CreatePermissionRequest a PermissionDomain", e);
        }
    }

    public static void applyUpdate(PermissionDomain existing, UpdatePermissionRequest req) {
        try {
            if (existing == null || req == null) return;
            if (req.name() != null) existing.updateName(new PermissionName(req.name()));
            if (req.description() != null) existing.updateDescription(new PermissionDescription(req.description()));
        } catch (Exception e) {
            throw new MappingException("Error aplicando UpdatePermissionRequest a PermissionDomain", e);
        }
    }

    public static PermissionResponse toResponse(PermissionDomain domain) {
        try {
            if (domain == null) return null;
            String name = domain.getName() == null ? null : domain.getName().value();
            String desc = domain.getDescription() == null ? null : domain.getDescription().description();
            String status = domain.getStatus() == null ? null : domain.getStatus().name();

            return new PermissionResponse(domain.getPermissionId().id(), name, desc, status);
        } catch (Exception e) {
            throw new MappingException("Error mapeando PermissionDomain a PermissionResponse", e);
        }
    }
}
