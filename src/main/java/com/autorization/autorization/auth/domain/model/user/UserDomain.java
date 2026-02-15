package com.autorization.autorization.auth.domain.model.user;

import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.user.vo.*;
import com.autorization.autorization.shared.domain.exception.NullValueException;
import com.autorization.autorization.auth.domain.exception.RoleAlreadyAssignedException;
import com.autorization.autorization.auth.domain.exception.RoleNotAssignedException;


import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

public class UserDomain {

    private final UserId userId;
    private UserNames names;
    private UserEmail email;
    private UserPassword password;
    private AccountStatus status;
    private final Set<RoleDomain> roles;

    public UserDomain(UserId userId, UserNames names, UserEmail email, UserPassword password, AccountStatus status, Set<RoleDomain> roles) {
        if (userId == null) {
            throw new NullValueException("userId");
        }
        if (names == null) {
            throw new NullValueException("names");
        }

        this.userId = userId;
        this.names = names;
        this.email = email;
        this.password = password;
        this.status = status;
        // Copia defensiva del set de roles
        this.roles = roles == null ? new HashSet<>() : new HashSet<>(roles);
    }

    public UserId getUserId() {
        return userId;
    }

    public UserNames getNames() {
        return names;
    }

    public UserEmail getEmail() {
        return email;
    }

    public UserPassword getPassword() {
        return password;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Set<RoleDomain> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean hasRoles() {
        return roles != null && !roles.isEmpty();
    }

    // Métodos de negocio para modificar estado de forma controlada
    public void changeNames(UserNames names) {
        this.names = names;
    }

    public void changeEmail(UserEmail newEmail) {
        this.email = newEmail;
    }

    public void changePassword(UserPassword newPassword) {
        this.password = newPassword;
    }

    public void changeStatus(AccountStatus newStatus) {
        this.status = newStatus;
    }

    public void addRole(RoleDomain role) {
        if (role == null) {
            throw new NullValueException("role");
        }
        boolean exists = roles.stream()
                .map(RoleDomain::getRoleId)
                .anyMatch(rid -> rid.equals(role.getRoleId()));
        if (exists) {
            throw new RoleAlreadyAssignedException("El usuario ya tiene ese rol");
        }
        this.roles.add(role);
    }

    public void removeRole(RoleDomain role) {
        if (role == null) {
            throw new NullValueException("role");
        }
        boolean exists = roles.stream()
                .map(RoleDomain::getRoleId)
                .anyMatch(rid -> rid.equals(role.getRoleId()));
        if (!exists) {
            throw new RoleNotAssignedException("El usuario no tiene ese rol");
        }
        this.roles.removeIf(r -> r.getRoleId().equals(role.getRoleId()));
    }
}
