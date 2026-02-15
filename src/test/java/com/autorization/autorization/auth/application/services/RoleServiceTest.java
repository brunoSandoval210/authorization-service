package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.CreateRoleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateRoleRequest;
import com.autorization.autorization.auth.application.dto.out.RoleResponse;
import com.autorization.autorization.auth.domain.exception.RoleAlreadyExistsException;
import com.autorization.autorization.auth.domain.exception.RoleNotFoundException;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.role.vo.RoleDescription;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.model.role.vo.RoleName;
import com.autorization.autorization.auth.domain.port.out.PermissionRepositoryPort;
import com.autorization.autorization.auth.domain.port.out.RoleRepositoryPort;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private PermissionRepositoryPort permissionRepositoryPort;

    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("should create role successfully when name is unique")
    void shouldCreateRoleSuccessfully() {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("ADMIN", "Administrator role");
        RoleDomain roleDomain = new RoleDomain(new RoleId(UUID.randomUUID()), new RoleName("ADMIN"),
                new RoleDescription("Administrator role"), new HashSet<>(), Status.ACTIVO);

        given(roleRepositoryPort.findByName(any(RoleName.class))).willReturn(Optional.empty());
        given(roleRepositoryPort.save(any(RoleDomain.class))).willReturn(roleDomain);

        // When
        RoleResponse response = roleService.create(request);

        // Then
        assertNotNull(response);
        assertEquals("ADMIN", response.name());
        then(roleRepositoryPort).should().save(any(RoleDomain.class));
    }

    @Test
    @DisplayName("should throw exception when creating role with existing name")
    void shouldThrowExceptionWhenCreatingExistingRole() {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("ADMIN", "Administrator role");
        RoleDomain existingRole = new RoleDomain(new RoleId(UUID.randomUUID()), new RoleName("ADMIN"),
                new RoleDescription("Desc"), new HashSet<>(), Status.ACTIVO);

        given(roleRepositoryPort.findByName(any(RoleName.class))).willReturn(Optional.of(existingRole));

        // When & Then
        assertThrows(RoleAlreadyExistsException.class, () -> roleService.create(request));
        then(roleRepositoryPort).should(never()).save(any(RoleDomain.class));
    }

    @Test
    @DisplayName("should update role successfully")
    void shouldUpdateRoleSuccessfully() {
        // Given
        UUID id = UUID.randomUUID();
        UpdateRoleRequest request = new UpdateRoleRequest("NEW_ADMIN", "New Description");
        RoleDomain existingRole = new RoleDomain(new RoleId(id), new RoleName("ADMIN"), new RoleDescription("Desc"),
                new HashSet<>(), Status.ACTIVO);
        RoleDomain updatedRole = new RoleDomain(new RoleId(id), new RoleName("NEW_ADMIN"),
                new RoleDescription("New Description"), new HashSet<>(), Status.ACTIVO);

        given(roleRepositoryPort.findById(any(RoleId.class))).willReturn(Optional.of(existingRole));
        given(roleRepositoryPort.save(any(RoleDomain.class))).willReturn(updatedRole);

        // When
        RoleResponse response = roleService.update(id, request);

        // Then
        assertNotNull(response);
        assertEquals("NEW_ADMIN", response.name());
        assertEquals("New Description", response.description());
    }

    @Test
    @DisplayName("should throw exception when updating non-existent role")
    void shouldThrowExceptionWhenUpdatingNonExistentRole() {
        // Given
        UUID id = UUID.randomUUID();
        UpdateRoleRequest request = new UpdateRoleRequest("NEW_ADMIN", "Desc");

        given(roleRepositoryPort.findById(any(RoleId.class))).willReturn(Optional.empty());

        // When & Then
        assertThrows(RoleNotFoundException.class, () -> roleService.update(id, request));
    }

    @Test
    @DisplayName("should find role by id")
    void shouldFindById() {
        // Given
        UUID id = UUID.randomUUID();
        RoleDomain role = new RoleDomain(new RoleId(id), new RoleName("ADMIN"), new RoleDescription("Desc"),
                new HashSet<>(), Status.ACTIVO);

        given(roleRepositoryPort.findById(any(RoleId.class))).willReturn(Optional.of(role));

        // When
        Optional<RoleResponse> response = roleService.findById(id);

        // Then
        assertTrue(response.isPresent());
        assertEquals(id, response.get().id());
    }

    @Test
    @DisplayName("should deactivate role")
    void shouldDeactivateRole() {
        // Given
        UUID id = UUID.randomUUID();
        RoleDomain role = new RoleDomain(new RoleId(id), new RoleName("ADMIN"), new RoleDescription("Desc"),
                new HashSet<>(), Status.ACTIVO);

        given(roleRepositoryPort.findById(any(RoleId.class))).willReturn(Optional.of(role));

        // When
        roleService.deactivate(id);

        // Then
        then(roleRepositoryPort).should().updateEnabled(any(RoleId.class), booleanThat(b -> !b));
    }

    // Helper needed for boolean matcher if ArgumentMatchers.eq(false) is ambiguous
    // or issues arise,
    // but typically eq(false) works. Using custom matcher/lambda for clarity if
    // needed.
    // simpler:
    // then(roleRepositoryPort).should().updateEnabled(new RoleId(id), false);
    // But since RoleId is value object, verify equality might need correct equals.
    // Let's use any() for ID in verify to avoid equals issues if not strictly
    // tested here.
    private boolean booleanThat(java.util.function.Predicate<Boolean> matcher) {
        return org.mockito.ArgumentMatchers.booleanThat(matcher::test);
    }
}
