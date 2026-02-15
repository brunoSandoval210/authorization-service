package com.autorization.autorization.auth.adapter.out.jpa;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Role;

import com.autorization.autorization.auth.adapter.out.jpa.repository.PermissionRepository;
import com.autorization.autorization.auth.adapter.out.jpa.repository.RoleRepository;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.role.vo.RoleDescription;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.model.role.vo.RoleName;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryAdapterTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleRepositoryAdapter adapter;

    @Test
    @DisplayName("should save new role")
    void shouldSaveNewRole() {
        // Given
        RoleDomain domain = new RoleDomain(new RoleId(UUID.randomUUID()), new RoleName("ADMIN"),
                new RoleDescription("Desc"), new HashSet<>(), Status.ACTIVO);
        Role entity = new Role();
        entity.setRoleId(domain.getRoleId().id());
        entity.setName("ADMIN");
        entity.setDescription("Desc");
        entity.setStatus(Status.ACTIVO);
        entity.setPermissions(new HashSet<>());

        given(roleRepository.findById(any(UUID.class))).willReturn(Optional.empty());
        given(roleRepository.save(any(Role.class))).willReturn(entity);

        // When
        RoleDomain saved = adapter.save(domain);

        // Then
        assertNotNull(saved);
        assertEquals("ADMIN", saved.getName().value());
        then(roleRepository).should().findById(domain.getRoleId().id());
        then(roleRepository).should().save(any(Role.class));
    }

    @Test
    @DisplayName("should find role by id")
    void shouldFindById() {
        // Given
        UUID id = UUID.randomUUID();
        Role entity = new Role();
        entity.setRoleId(id);
        entity.setName("ADMIN");
        entity.setDescription("Desc");
        entity.setStatus(Status.ACTIVO);
        entity.setPermissions(new HashSet<>());

        given(roleRepository.findById(id)).willReturn(Optional.of(entity));

        // When
        Optional<RoleDomain> result = adapter.findById(new RoleId(id));

        // Then
        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName().value());
    }

    @Test
    @DisplayName("should delete role by id")
    void shouldDeleteById() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        adapter.deleteById(new RoleId(id));

        // Then
        then(roleRepository).should().deleteById(id);
    }
}
