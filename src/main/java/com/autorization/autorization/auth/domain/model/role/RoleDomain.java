package com.autorization.autorization.auth.domain.model.role;

import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.role.vo.*;
import com.autorization.autorization.shared.domain.model.Status;
import com.autorization.autorization.shared.domain.exception.NullValueException;
import com.autorization.autorization.auth.domain.exception.PermissionAlreadyAssignedException;
import com.autorization.autorization.auth.domain.exception.PermissionNotAssignedException;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

public class RoleDomain {
    private final RoleId roleId;
    private RoleName name;
    private RoleDescription description;
    private final Set<PermissionDomain> permissions;
    private Status status;

    public RoleDomain(RoleId roleId, RoleName name, RoleDescription description, Set<PermissionDomain> permissions, Status status) {
        if (roleId == null) {
            throw new NullValueException("roleId");
        }
        this.roleId = roleId;
        this.name = name;
        this.description = description;
        this.permissions = permissions == null ? new HashSet<>() : new HashSet<>(permissions);
        this.status = status;
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public RoleName getName() {
        return name;
    }

    public RoleDescription getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Set<PermissionDomain> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void addPermission(PermissionDomain permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission no puede ser nulo");
        }
        // comprobar existencia por id
        boolean exists = permissions.stream()
                .map(PermissionDomain::getPermissionId)
                .anyMatch(pid -> pid.equals(permission.getPermissionId()));
        if (exists) {
            throw new PermissionAlreadyAssignedException("El permiso ya está asignado al rol");
        }
        this.permissions.add(permission);
    }

    public void removePermission(PermissionDomain permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission no puede ser nulo");
        }
        boolean exists = permissions.stream()
                .map(PermissionDomain::getPermissionId)
                .anyMatch(pid -> pid.equals(permission.getPermissionId()));
        if (!exists) {
            throw new PermissionNotAssignedException("El permiso no está asignado al rol");
        }
        this.permissions.removeIf(p -> p.getPermissionId().equals(permission.getPermissionId()));
    }

    public void updateName(RoleName newName) {
        if (newName == null) {
            throw new NullValueException("roleName");
        }
        this.name = newName;
    }

    public void updateDescription(RoleDescription newDescription) {
        this.description = newDescription;
    }
}
