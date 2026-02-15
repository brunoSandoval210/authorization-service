package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.CreateModuleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateModuleRequest;
import com.autorization.autorization.auth.application.dto.out.ModuleResponse;
import com.autorization.autorization.auth.domain.exception.ModuleNotFoundException;
import com.autorization.autorization.auth.domain.model.module.ModuleDomain;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleIcon;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleName;
import com.autorization.autorization.auth.domain.model.module.vo.ModulePath;
import com.autorization.autorization.auth.domain.port.out.ModuleRepositoryPort;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepositoryPort;

    @InjectMocks
    private ModuleService moduleService;

    @Test
    @DisplayName("should create module successfully")
    void shouldCreateModule() {
        CreateModuleRequest request = new CreateModuleRequest("Users", "/users", "user-icon");
        ModuleDomain domain = createDomain(UUID.randomUUID(), "Users");

        given(moduleRepositoryPort.save(any(ModuleDomain.class))).willReturn(domain);

        ModuleResponse response = moduleService.create(request);

        assertNotNull(response);
        assertEquals("Users", response.name());
        then(moduleRepositoryPort).should().save(any(ModuleDomain.class));
    }

    @Test
    @DisplayName("should update module")
    void shouldUpdateModule() {
        UUID id = UUID.randomUUID();
        UpdateModuleRequest request = new UpdateModuleRequest("New Users", "/new-users", "new-icon");
        ModuleDomain existing = createDomain(id, "Users");
        ModuleDomain updated = createDomain(id, "New Users");

        given(moduleRepositoryPort.findById(any(ModuleId.class))).willReturn(Optional.of(existing));
        given(moduleRepositoryPort.save(any(ModuleDomain.class))).willReturn(updated);

        ModuleResponse response = moduleService.update(id, request);

        assertEquals("New Users", response.name());
    }

    @Test
    @DisplayName("should throw exception when updating non-existent module")
    void shouldThrowExceptionWhenUpdatingNonExistent() {
        UUID id = UUID.randomUUID();
        UpdateModuleRequest request = new UpdateModuleRequest("Users", null, null);

        given(moduleRepositoryPort.findById(any(ModuleId.class))).willReturn(Optional.empty());

        assertThrows(ModuleNotFoundException.class, () -> moduleService.update(id, request));
    }

    @Test
    @DisplayName("should deactivate module")
    void shouldDeactivateModule() {
        UUID id = UUID.randomUUID();
        ModuleDomain existing = createDomain(id, "Users");

        given(moduleRepositoryPort.findById(any(ModuleId.class))).willReturn(Optional.of(existing));

        moduleService.deactivate(id);

        then(moduleRepositoryPort).should().updateEnabled(any(ModuleId.class), eq(false));
    }

    // Helper to match boolean
    private boolean eq(boolean b) {
        return org.mockito.ArgumentMatchers.eq(b);
    }

    private ModuleDomain createDomain(UUID id, String name) {
        return new ModuleDomain(
                new ModuleId(id),
                new ModuleName(name),
                new ModulePath("/users"),
                new ModuleIcon("icon"),
                Status.ACTIVO);
    }
}
