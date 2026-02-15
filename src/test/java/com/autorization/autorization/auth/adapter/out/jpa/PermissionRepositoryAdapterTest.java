package com.autorization.autorization.auth.adapter.out.jpa;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Module;
import com.autorization.autorization.auth.adapter.out.jpa.entity.Permission;
import com.autorization.autorization.auth.adapter.out.jpa.repository.ModuleRepository;
import com.autorization.autorization.auth.adapter.out.jpa.repository.PermissionRepository;
import com.autorization.autorization.auth.domain.model.permission.PermissionDomain;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionDescription;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionId;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionModule;
import com.autorization.autorization.auth.domain.model.permission.vo.PermissionName;
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
class PermissionRepositoryAdapterTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private PermissionRepositoryAdapter adapter;

    @Test
    @DisplayName("should save new permission")
    void shouldSaveNewPermission() {
        // Given
        UUID moduleId = UUID.randomUUID();
        PermissionDomain domain = new PermissionDomain(
                new PermissionId(UUID.randomUUID()),
                new PermissionName("READ"),
                new PermissionDescription("Desc"),
                new PermissionModule(moduleId, "USERS"),
                Status.ACTIVO);
        Permission entity = new Permission();
        entity.setPermissionId(domain.getPermissionId().id());
        entity.setName("READ");
        entity.setDescription("Desc");
        entity.setStatus(Status.ACTIVO);

        given(permissionRepository.findById(any(UUID.class))).willReturn(Optional.empty());
        given(moduleRepository.getReferenceById(moduleId)).willReturn(new Module());
        given(permissionRepository.save(any(Permission.class))).willReturn(entity);

        // When
        PermissionDomain saved = adapter.save(domain);

        // Then
        assertNotNull(saved);
        assertEquals("READ", saved.getName().value());
        then(permissionRepository).should().save(any(Permission.class));
    }

    @Test
    @DisplayName("should find permission by id")
    void shouldFindById() {
        // Given
        UUID id = UUID.randomUUID();
        Permission entity = new Permission();
        entity.setPermissionId(id);
        entity.setName("READ");
        entity.setDescription("Desc");
        entity.setStatus(Status.ACTIVO);

        given(permissionRepository.findById(id)).willReturn(Optional.of(entity));

        // When
        Optional<PermissionDomain> result = adapter.findById(new PermissionId(id));

        // Then
        assertTrue(result.isPresent());
        assertEquals("READ", result.get().getName().value());
    }

    @Test
    @DisplayName("should delete permission by id")
    void shouldDeleteById() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        adapter.deleteById(new PermissionId(id));

        // Then
        then(permissionRepository).should().deleteById(id);
    }
}
