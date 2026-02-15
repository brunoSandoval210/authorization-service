package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.CreatePermissionRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdatePermissionRequest;
import com.autorization.autorization.auth.application.dto.out.PermissionResponse;
import com.autorization.autorization.auth.domain.exception.PermissionAlreadyExistsException;

import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionDescription;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionModule;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;
import com.autorization.autorization.auth.domain.port.out.PermissionRepositoryPort;
import com.autorization.autorization.security.util.JwtUtil;
import com.autorization.autorization.shared.domain.model.Status;
import com.autorization.autorization.shared.infraestructure.logging.LogControlService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepositoryPort permissionRepositoryPort;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    @DisplayName("should create permission successfully")
    void shouldCreatePermission() {
        CreatePermissionRequest request = new CreatePermissionRequest("READ", "Read access");
        PermissionDomain domain = createDomain(UUID.randomUUID(), "READ");

        given(permissionRepositoryPort.findByName(any(PermissionName.class))).willReturn(Optional.empty());
        given(permissionRepositoryPort.save(any(PermissionDomain.class))).willReturn(domain);

        PermissionResponse response = permissionService.create(request);

        assertNotNull(response);
        assertEquals("READ", response.name());
        then(permissionRepositoryPort).should().save(any(PermissionDomain.class));
    }

    @Test
    @DisplayName("should throw exception when creating existing permission")
    void shouldThrowExceptionWhenCreatingExisting() {
        CreatePermissionRequest request = new CreatePermissionRequest("READ", "Desc");
        PermissionDomain existing = createDomain(UUID.randomUUID(), "READ");

        given(permissionRepositoryPort.findByName(any(PermissionName.class))).willReturn(Optional.of(existing));

        assertThrows(PermissionAlreadyExistsException.class, () -> permissionService.create(request));
        then(permissionRepositoryPort).should(never()).save(any(PermissionDomain.class));
    }

    @Test
    @DisplayName("should update permission")
    void shouldUpdatePermission() {
        UUID id = UUID.randomUUID();
        UpdatePermissionRequest request = new UpdatePermissionRequest("WRITE", "Write access");
        PermissionDomain existing = createDomain(id, "READ");
        PermissionDomain updated = createDomain(id, "WRITE");

        given(permissionRepositoryPort.findById(any(PermissionId.class))).willReturn(Optional.of(existing));
        given(permissionRepositoryPort.save(any(PermissionDomain.class))).willReturn(updated);

        PermissionResponse response = permissionService.update(id, request);

        assertEquals("WRITE", response.name());
    }

    @Test
    @DisplayName("should deactivate permission")
    void shouldDeactivatePermission() {
        UUID id = UUID.randomUUID();
        PermissionDomain existing = createDomain(id, "READ");

        given(permissionRepositoryPort.findById(any(PermissionId.class))).willReturn(Optional.of(existing));

        permissionService.deactivate(id);

        then(permissionRepositoryPort).should().updateEnabled(any(PermissionId.class), eq(false));
    }

    // Helper to match boolean
    private boolean eq(boolean b) {
        return ArgumentMatchers.eq(b);
    }

    private PermissionDomain createDomain(UUID id, String name) {
        return new PermissionDomain(
                new PermissionId(id),
                new PermissionName(name),
                new PermissionDescription("Desc"),
                new PermissionModule(UUID.randomUUID(), "USERS"),
                Status.ACTIVO);
    }
}
