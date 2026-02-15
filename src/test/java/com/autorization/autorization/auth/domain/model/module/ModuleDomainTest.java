package com.autorization.autorization.auth.domain.model.module;

import com.autorization.autorization.auth.domain.model.module.vo.ModuleIcon;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleName;
import com.autorization.autorization.auth.domain.model.module.vo.ModulePath;
import com.autorization.autorization.shared.domain.exception.NullValueException;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModuleDomainTest {

    @Test
    @DisplayName("should create module with valid data")
    void shouldCreateModuleWithValidData() {
        ModuleId id = new ModuleId(UUID.randomUUID());
        ModuleName name = new ModuleName("Users");
        ModulePath path = new ModulePath("/users");
        ModuleIcon icon = new ModuleIcon("user-icon");
        Status status = Status.ACTIVO;

        ModuleDomain module = new ModuleDomain(id, name, path, icon, status);

        assertNotNull(module);
        assertEquals(id, module.getModuleId());
        assertEquals(name, module.getName());
        assertEquals(path, module.getPath());
        assertEquals(icon, module.getIcon());
        assertEquals(status, module.getStatus());
    }

    @Test
    @DisplayName("should throw exception when moduleId is null")
    void shouldThrowExceptionWhenModuleIdIsNull() {
        assertThrows(NullValueException.class, () -> new ModuleDomain(
                null,
                new ModuleName("Users"),
                new ModulePath("/users"),
                new ModuleIcon("icon"),
                Status.ACTIVO));
    }

    @Test
    @DisplayName("should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(NullValueException.class, () -> new ModuleName(null));
    }

    @Test
    @DisplayName("should throw exception when name is empty")
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new ModuleName(""));
        assertThrows(IllegalArgumentException.class, () -> new ModuleName("   "));
    }

    @Test
    @DisplayName("should throw exception when path is invalid")
    void shouldThrowExceptionWhenPathIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new ModulePath("users")); // invalid start
        assertThrows(IllegalArgumentException.class, () -> new ModulePath(null));
    }

    @Test
    @DisplayName("should update name successfully")
    void shouldUpdateName() {
        ModuleDomain module = createModule();
        ModuleName newName = new ModuleName("New Users");

        module.updateName(newName);

        assertEquals(newName, module.getName());
    }

    @Test
    @DisplayName("should update path successfully")
    void shouldUpdatePath() {
        ModuleDomain module = createModule();
        ModulePath newPath = new ModulePath("/new-users");

        module.updatePath(newPath);

        assertEquals(newPath, module.getPath());
    }

    @Test
    @DisplayName("should update icon successfully")
    void shouldUpdateIcon() {
        ModuleDomain module = createModule();
        ModuleIcon newIcon = new ModuleIcon("new-icon");

        module.updateIcon(newIcon);

        assertEquals(newIcon, module.getIcon());
    }

    private ModuleDomain createModule() {
        return new ModuleDomain(
                new ModuleId(UUID.randomUUID()),
                new ModuleName("Users"),
                new ModulePath("/users"),
                new ModuleIcon("icon"),
                Status.ACTIVO);
    }
}
