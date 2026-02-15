package com.autorization.autorization.auth.adapter.out.jpa.mapper;

import com.autorization.autorization.auth.adapter.out.jpa.entity.User;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.*;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserJPAMapperTest {

    @Test
    @DisplayName("Given UserEntity, When toDomain, Then UserDomain is returned with correct values")
    void shouldMapToDomain() {
        // Given
        UUID userId = UUID.randomUUID();
        User entity = new User();
        entity.setUserId(userId);
        entity.setName("John");
        entity.setLastName("Doe");
        entity.setEmail("john@example.com");
        entity.setPassword("pass1234567");
        entity.setEnabled(true);
        entity.setStatus(Status.ACTIVO);
        entity.setRoles(new HashSet<>());

        // When
        UserDomain domain = UserJPAMapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertEquals(userId, domain.getUserId().id());
        assertEquals("John", domain.getNames().name());
        assertEquals("john@example.com", domain.getEmail().value());
        assertTrue(domain.getStatus().isEnabled());
    }

    @Test
    @DisplayName("Given null Entity, When toDomain, Then returns null")
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(UserJPAMapper.toDomain(null));
    }

    @Test
    @DisplayName("Given UserDomain, When fromDomain, Then UserEntity is returned with correct values")
    void shouldMapFromDomain() {
        // Given
        UserDomain domain = new UserDomain(
                new UserId(UUID.randomUUID()),
                new UserNames("Jane", "Doe", null),
                new UserEmail("jane@example.com"),
                new UserPassword("pass1234567"),
                new AccountStatus(true, true, true, true, Status.ACTIVO),
                new HashSet<>());

        // When
        User entity = UserJPAMapper.fromDomain(domain);

        // Then
        assertNotNull(entity);
        assertEquals(domain.getUserId().id(), entity.getUserId());
        assertEquals("Jane", entity.getName());
        assertTrue(entity.isEnabled());
    }
}
