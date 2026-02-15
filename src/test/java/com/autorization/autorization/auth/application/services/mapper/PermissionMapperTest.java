package com.autorization.autorization.auth.application.services.mapper;

import com.autorization.autorization.auth.adapter.in.web.request.CreatePermissionRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdatePermissionRequest;
import com.autorization.autorization.auth.application.dto.out.PermissionResponse;
import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionDescription;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionModule;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PermissionMapperTest {

    @Test
    @DisplayName("should map CreateRequest to Domain")
    void shouldMapCreateToDomain() {
        CreatePermissionRequest request = new CreatePermissionRequest("READ", "Desc");
        PermissionDomain domain = PermissionMapper.toDomain(request);

        assertNotNull(domain);
        assertEquals("READ", domain.getName().value());
        assertEquals("Desc", domain.getDescription().description());
    }

    @Test
    @DisplayName("should map update request to existing domain")
    void shouldMapUpdate() {
        PermissionDomain domain = new PermissionDomain(
                new PermissionId(UUID.randomUUID()),
                new PermissionName("READ"),
                new PermissionDescription("Desc"),
                new PermissionModule(UUID.randomUUID(), "USERS"),
                Status.ACTIVO);
        UpdatePermissionRequest request = new UpdatePermissionRequest("WRITE", "New Desc");

        PermissionMapper.applyUpdate(domain, request);

        assertEquals("WRITE", domain.getName().value());
        assertEquals("New Desc", domain.getDescription().description());
    }

    @Test
    @DisplayName("should map Domain to Response")
    void shouldMapDomainToResponse() {
        PermissionDomain domain = new PermissionDomain(
                new PermissionId(UUID.randomUUID()),
                new PermissionName("READ"),
                new PermissionDescription("Desc"),
                new PermissionModule(UUID.randomUUID(), "USERS"),
                Status.ACTIVO);

        PermissionResponse response = PermissionMapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(domain.getPermissionId().id(), response.id());
        assertEquals("READ", response.name());
        assertEquals("Desc", response.description());
        assertEquals("ACTIVO", response.status());
    }
}
