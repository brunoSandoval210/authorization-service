package com.autorization.autorization.auth.application.services.mapper;

import com.autorization.autorization.auth.adapter.in.web.request.CreateUserRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateUserRequest;
import com.autorization.autorization.auth.application.dto.out.PermissionResponse;
import com.autorization.autorization.auth.application.dto.out.RoleResponse;
import com.autorization.autorization.auth.application.dto.out.UserResponse;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.*;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.adapter.out.jpa.mapper.exception.MappingException;
import com.autorization.autorization.shared.domain.model.Status;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserMapper {
    // Crear dominio desde request de creación
    public static UserDomain toDomain(CreateUserRequest request) {
        try {
            // crear status por defecto ACTIVO si no viene
            AccountStatus defaultStatus = new AccountStatus(true, true, true, true, Status.ACTIVO);

            return new UserDomain(
                    new UserId(UUID.randomUUID()),
                    new UserNames(request.name(), request.lastName(), request.secondName()),
                    request.email() == null ? null : new UserEmail(request.email()),
                    request.password() == null ? null : new UserPassword(request.password()),
                    defaultStatus,
                    null
            );
        } catch (Exception e) {
            throw new MappingException("Error mapeando CreateUserRequest a UserDomain", e);
        }
    }

    // Aplicar cambios parciales desde request de actualización al dominio existente
    public static void applyUpdate(UserDomain existing, UpdateUserRequest req) {
        try {
            if (existing == null || req == null) return;

            // aplicar nombres si vienen
            if (req.name() != null || req.lastName() != null || req.secondName() != null) {
                String name = req.name() != null ? req.name() : existing.getNames().name();
                String lastName = req.lastName() != null ? req.lastName() : existing.getNames().lastName();
                String secondName = req.secondName() != null ? req.secondName() : existing.getNames().secondName();
                existing.changeNames(new UserNames(name, lastName, secondName));
            }

            // aplicar contraseña si viene
            if (req.password() != null) {
                existing.changePassword(new UserPassword(req.password()));
            }

            // aplicar email si viene (la validación de unicidad debe hacerse en el service antes de llamar aquí)
            if (req.email() != null) {
                existing.changeEmail(new UserEmail(req.email()));
            }
        } catch (Exception e) {
            throw new MappingException("Error aplicando UpdateUserRequest a UserDomain", e);
        }
    }

    // Mapear UserDomain -> UserResponse
    public static UserResponse toResponse(UserDomain domain) {
        try {
            if (domain == null) return null;

            String namesConcat = domain.getNames() == null ? "" : (
                    domain.getNames().name() + " " + domain.getNames().lastName() + (domain.getNames().secondName() == null ? "" : " " + domain.getNames().secondName())
            );

            List<RoleResponse> roles = domain.getRoles() == null ? List.of() : domain.getRoles().stream()
                    .map(UserMapper::roleToResponse)
                    .collect(Collectors.toList());

            var status = domain.getStatus();
            boolean enabled = status != null && status.isEnabled();
            boolean accountNonExpired = status != null && status.accountNonExpired();
            boolean accountNonLocked = status != null && status.accountNonLocked();
            boolean credentialsNonExpired = status != null && status.credentialsNonExpired();

            return new UserResponse(
                    domain.getUserId().id(),
                    namesConcat.trim(),
                    domain.getEmail() == null ? null : domain.getEmail().value(),
                    enabled,
                    accountNonExpired,
                    accountNonLocked,
                    credentialsNonExpired,
                    roles,
                    domain.getStatus().status().name()
            );
        } catch (Exception e) {
            throw new MappingException("Error mapeando UserDomain a UserResponse", e);
        }
    }

    private static RoleResponse roleToResponse(RoleDomain r) {
        try {
            if (r == null) return null;
            String name = r.getName() == null ? null : r.getName().value();
            String desc = r.getDescription() == null ? null : r.getDescription().description();

            List<PermissionResponse> perms = r.getPermissions() == null ? List.of() : r.getPermissions().stream()
                    .map(p -> new PermissionResponse(
                            p.getPermissionId().id(),
                            p.getName() == null ? null : p.getName().value(),
                            p.getDescription() == null ? null : p.getDescription().description(),
                            p.getStatus().name()))
                    .collect(Collectors.toList());

            return new RoleResponse(r.getRoleId().id(), name, desc, perms,r.getStatus().name());
        } catch (Exception e) {
            throw new MappingException("Error mapeando RoleDomain a RoleResponse (dentro de UserMapper)", e);
        }
    }
}
