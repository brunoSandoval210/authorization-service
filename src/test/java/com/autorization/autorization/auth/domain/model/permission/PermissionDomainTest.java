package com.autorization.autorization.auth.domain.model.permission;

import com.autorization.autorization.auth.domain.model.permission.vo.PermissionDescription;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionModule;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;
import com.autorization.autorization.shared.domain.exception.NullValueException;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PermissionDomainTest {

    @Test
    @DisplayName("should create permission with valid data")
    void shouldCreatePermissionWithValidData() {
        PermissionId id = new PermissionId(UUID.randomUUID());
        PermissionName name = new PermissionName("READ");
        PermissionDescription description = new PermissionDescription("Read access");
        PermissionModule module = new PermissionModule(UUID.randomUUID(), "USERS");
        Status status = Status.ACTIVO;

        PermissionDomain permission = new PermissionDomain(id, name, description, module, status);

        assertNotNull(permission);
        assertEquals(id, permission.getPermissionId());
        assertEquals(name, permission.getName());
        assertEquals(description, permission.getDescription());
        assertEquals(module, permission.getModule());
        assertEquals(status, permission.getStatus());
    }

    @Test
    @DisplayName("should throw exception when permissionId is null")
    void shouldThrowExceptionWhenPermissionIdIsNull() {
        assertThrows(NullValueException.class, () -> new PermissionDomain(
                null,
                new PermissionName("READ"),
                new PermissionDescription("Desc"),
                new PermissionModule(UUID.randomUUID(), "USERS"),
                Status.ACTIVO));
    }

    @Test
    @DisplayName("should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(NullValueException.class, () -> new PermissionDomain(
                new PermissionId(UUID.randomUUID()),
                null,
                new PermissionDescription("Desc"),
                new PermissionModule(UUID.randomUUID(), "USERS"),
                Status.ACTIVO));
    }

    @Test
    @DisplayName("should throw exception when name is empty")
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new PermissionName(""));
        assertThrows(IllegalArgumentException.class, () -> new PermissionName("   "));
    }

    @Test
    @DisplayName("should update name successfully")
    void shouldUpdateName() {
        PermissionDomain permission = createPermission();
        PermissionName newName = new PermissionName("WRITE");

        permission.updateName(newName);

        assertEquals(newName, permission.getName());
    }

    @Test
    @DisplayName("should throw exception when updating name with null")
    void shouldThrowExceptionWhenUpdatingNameWithNull() {
        PermissionDomain permission = createPermission();
        assertThrows(NullValueException.class, () -> permission.updateName(null));
    }

    @Test
    @DisplayName("should update description successfully")
    void shouldUpdateDescription() {
        PermissionDomain permission = createPermission();
        PermissionDescription newDescription = new PermissionDescription("New Desc");

        permission.updateDescription(newDescription);

        assertEquals(newDescription, permission.getDescription());
    }

    private PermissionDomain createPermission() {
        return new PermissionDomain(
                new PermissionId(UUID.randomUUID()),
                new PermissionName("READ"),
                new PermissionDescription("Desc"),
                new PermissionModule(UUID.randomUUID(), "USERS"),
                Status.ACTIVO);
    }
}
