package com.autorization.autorization.auth.domain.model.role;

import com.autorization.autorization.auth.domain.exception.PermissionAlreadyAssignedException;
import com.autorization.autorization.auth.domain.exception.PermissionNotAssignedException;
import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.*;
import com.autorization.autorization.auth.domain.model.role.vo.RoleDescription;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.model.role.vo.RoleName;
import com.autorization.autorization.shared.domain.exception.NullValueException;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleDomainTest {

    @Test
    @DisplayName("should create role with valid data")
    void shouldCreateRoleWithValidData() {
        RoleId roleId = new RoleId(UUID.randomUUID());
        RoleName name = new RoleName("ADMIN");
        RoleDescription description = new RoleDescription("Administrator Role");
        Status status = Status.ACTIVO;

        RoleDomain role = new RoleDomain(roleId, name, description, new HashSet<>(), status);

        assertNotNull(role);
        assertEquals(roleId, role.getRoleId());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
        assertEquals(status, role.getStatus());
        assertTrue(role.getPermissions().isEmpty());
    }

    @Test
    @DisplayName("should throw exception when roleId is null")
    void shouldThrowExceptionWhenRoleIdIsNull() {
        assertThrows(NullValueException.class, () -> new RoleDomain(
                null,
                new RoleName("ADMIN"),
                new RoleDescription("Desc"),
                new HashSet<>(),
                Status.ACTIVO));
    }

    @Test
    @DisplayName("should add permission successfully")
    void shouldAddPermission() {
        RoleDomain role = createRole();
        PermissionDomain permission = createPermission(UUID.randomUUID());

        role.addPermission(permission);

        assertTrue(role.getPermissions().contains(permission));
    }

    @Test
    @DisplayName("should throw exception when adding existing permission")
    void shouldThrowExceptionWhenAddingExistingPermission() {
        RoleDomain role = createRole();
        PermissionDomain permission = createPermission(UUID.randomUUID());
        role.addPermission(permission);

        assertThrows(PermissionAlreadyAssignedException.class, () -> role.addPermission(permission));
    }

    @Test
    @DisplayName("should remove permission successfully")
    void shouldRemovePermission() {
        RoleDomain role = createRole();
        PermissionDomain permission = createPermission(UUID.randomUUID());
        role.addPermission(permission);

        role.removePermission(permission);

        assertFalse(role.getPermissions().contains(permission));
    }

    @Test
    @DisplayName("should throw exception when removing non-assigned permission")
    void shouldThrowExceptionWhenRemovingNonAssignedPermission() {
        RoleDomain role = createRole();
        PermissionDomain permission = createPermission(UUID.randomUUID());

        assertThrows(PermissionNotAssignedException.class, () -> role.removePermission(permission));
    }

    @Test
    @DisplayName("should update name successfully")
    void shouldUpdateName() {
        RoleDomain role = createRole();
        RoleName newName = new RoleName("NEW_ADMIN");

        role.updateName(newName);

        assertEquals(newName, role.getName());
    }

    @Test
    @DisplayName("should throw exception when updating name with null")
    void shouldThrowExceptionWhenUpdatingNameWithNull() {
        RoleDomain role = createRole();
        assertThrows(NullValueException.class, () -> role.updateName(null));
    }

    @Test
    @DisplayName("should update description successfully")
    void shouldUpdateDescription() {
        RoleDomain role = createRole();
        RoleDescription newDescription = new RoleDescription("New Description");

        role.updateDescription(newDescription);

        assertEquals(newDescription, role.getDescription());
    }

    // Helpers
    private RoleDomain createRole() {
        return new RoleDomain(
                new RoleId(UUID.randomUUID()),
                new RoleName("ADMIN"),
                new RoleDescription("Administrator"),
                new HashSet<>(),
                Status.ACTIVO);
    }

    private PermissionDomain createPermission(UUID id) {
        return new PermissionDomain(
                new PermissionId(id),
                new PermissionName("READ_PRIVILEGES"),
                new PermissionDescription("Read access"),
                new PermissionModule(UUID.randomUUID(),
                        "USERS"),
                Status.ACTIVO);
    }
}
