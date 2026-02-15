package com.autorization.autorization.auth.adapter.out.jpa;

import com.autorization.autorization.auth.adapter.out.jpa.entity.Module;
import com.autorization.autorization.auth.adapter.out.jpa.repository.ModuleRepository;
import com.autorization.autorization.auth.domain.model.module.ModuleDomain;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleIcon;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleId;
import com.autorization.autorization.auth.domain.model.module.vo.ModuleName;
import com.autorization.autorization.auth.domain.model.module.vo.ModulePath;
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
class ModuleRepositoryAdapterTest {

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ModuleRepositoryAdapter adapter;

    @Test
    @DisplayName("should save new module")
    void shouldSaveNewModule() {
        // Given
        ModuleDomain domain = new ModuleDomain(
                new ModuleId(UUID.randomUUID()),
                new ModuleName("Users"),
                new ModulePath("/users"),
                new ModuleIcon("icon"),
                Status.ACTIVO);
        Module entity = new Module();
        entity.setModuleId(domain.getModuleId().id());
        entity.setName("Users");
        entity.setPath("/users");
        entity.setIcon("icon");
        entity.setStatus(Status.ACTIVO);

        given(moduleRepository.findById(any(UUID.class))).willReturn(Optional.empty());
        given(moduleRepository.save(any(Module.class))).willReturn(entity);

        // When
        ModuleDomain saved = adapter.save(domain);

        // Then
        assertNotNull(saved);
        assertEquals("Users", saved.getName().name());
        then(moduleRepository).should().save(any(Module.class));
    }

    @Test
    @DisplayName("should find module by id")
    void shouldFindById() {
        // Given
        UUID id = UUID.randomUUID();
        Module entity = new Module();
        entity.setModuleId(id);
        entity.setName("Users");
        entity.setPath("/users");
        entity.setIcon("icon");
        entity.setStatus(Status.ACTIVO);

        given(moduleRepository.findById(id)).willReturn(Optional.of(entity));

        // When
        Optional<ModuleDomain> result = adapter.findById(new ModuleId(id));

        // Then
        assertTrue(result.isPresent());
        assertEquals("Users", result.get().getName().name());
    }

    @Test
    @DisplayName("should delete module by id")
    void shouldDeleteById() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        adapter.deleteById(new ModuleId(id));

        // Then
        then(moduleRepository).should().deleteById(id);
    }
}
