package com.autorization.autorization.auth.application.services.mapper;

import com.autorization.autorization.auth.adapter.in.web.request.CreateModuleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateModuleRequest;
import com.autorization.autorization.auth.application.dto.out.ModuleResponse;
import com.autorization.autorization.auth.domain.model.module.ModuleDomain;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleIcon;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleName;
import com.autorization.autorization.auth.domain.model.module.vo.ModulePath;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModuleMapperTest {

    @Test
    @DisplayName("should map CreateRequest to Domain")
    void shouldMapCreateToDomain() {
        CreateModuleRequest request = new CreateModuleRequest("Users", "/users", "icon");
        ModuleDomain domain = ModuleMapper.toDomain(request);

        assertNotNull(domain);
        assertEquals("Users", domain.getName().name());
        assertEquals("/users", domain.getPath().url());
        assertEquals("icon", domain.getIcon().icon());
    }

    @Test
    @DisplayName("should map update request to existing domain")
    void shouldMapUpdate() {
        ModuleDomain domain = new ModuleDomain(
                new ModuleId(UUID.randomUUID()),
                new ModuleName("Users"),
                new ModulePath("/users"),
                new ModuleIcon("icon"),
                Status.ACTIVO);
        UpdateModuleRequest request = new UpdateModuleRequest("New Users", "/new", "new-icon");

        ModuleMapper.applyUpdate(domain, request);

        assertEquals("New Users", domain.getName().name());
        assertEquals("/new", domain.getPath().url());
        assertEquals("new-icon", domain.getIcon().icon());
    }

    @Test
    @DisplayName("should map Domain to Response")
    void shouldMapDomainToResponse() {
        ModuleDomain domain = new ModuleDomain(
                new ModuleId(UUID.randomUUID()),
                new ModuleName("Users"),
                new ModulePath("/users"),
                new ModuleIcon("icon"),
                Status.ACTIVO);

        ModuleResponse response = ModuleMapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(domain.getModuleId().id(), response.id());
        assertEquals("Users", response.name());
        assertEquals("/users", response.path());
        assertEquals("icon", response.icon());
        assertEquals("ACTIVO", response.status());
    }
}
