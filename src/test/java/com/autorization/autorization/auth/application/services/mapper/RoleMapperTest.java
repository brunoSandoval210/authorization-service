package com.autorization.autorization.auth.application.services.mapper;

import com.autorization.autorization.auth.adapter.in.web.request.CreateRoleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateRoleRequest;
import com.autorization.autorization.auth.application.dto.out.RoleResponse;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.role.vo.RoleDescription;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.model.role.vo.RoleName;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleMapperTest {

    @Test
    @DisplayName("should map CreateRoleRequest to Domain")
    void shouldMapCreateRequestToDomain() {
        CreateRoleRequest request = new CreateRoleRequest("ADMIN", "Administrator");

        RoleDomain domain = RoleMapper.toDomain(request);

        assertNotNull(domain);
        assertEquals("ADMIN", domain.getName().value());
        assertEquals("Administrator", domain.getDescription().description());
        assertEquals(Status.ACTIVO, domain.getStatus());
    }

    @Test
    @DisplayName("should map Domain to Response")
    void shouldMapDomainToResponse() {
        RoleDomain domain = new RoleDomain(
                new RoleId(UUID.randomUUID()),
                new RoleName("ADMIN"),
                new RoleDescription("Administrator"),
                new HashSet<>(),
                Status.ACTIVO);

        RoleResponse response = RoleMapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(domain.getRoleId().id(), response.id());
        assertEquals("ADMIN", response.name());
        assertEquals("Administrator", response.description());
        assertEquals("ACTIVO", response.status());
    }

    @Test
    @DisplayName("should apply update to existing domain")
    void shouldApplyUpdate() {
        RoleDomain domain = new RoleDomain(
                new RoleId(UUID.randomUUID()),
                new RoleName("OLD"),
                new RoleDescription("Old Desc"),
                new HashSet<>(),
                Status.ACTIVO);

        UpdateRoleRequest request = new UpdateRoleRequest("NEW", "New Desc");

        RoleMapper.applyUpdate(domain, request);

        assertEquals("NEW", domain.getName().value());
        assertEquals("New Desc", domain.getDescription().description());
    }
}
