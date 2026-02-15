package com.autorization.autorization.auth.application.services.mapper;

import com.autorization.autorization.auth.adapter.in.web.request.CreateUserRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateUserRequest;
import com.autorization.autorization.auth.application.dto.out.UserResponse;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.*;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    @DisplayName("Given CreateUserRequest, When toDomain, Then returns valid UserDomain")
    void shouldMapCreateRequestToDomain() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "Doe", null, "john@example.com", "pass1234567");

        // When
        UserDomain domain = UserMapper.toDomain(request);

        // Then
        assertNotNull(domain);
        assertEquals("John", domain.getNames().name());
        assertEquals("john@example.com", domain.getEmail().value());
        assertEquals("pass1234567", domain.getPassword().value());
        assertTrue(domain.getStatus().isEnabled());
    }

    @Test
    @DisplayName("Given UpdateUserRequest, When applyUpdate, Then updates existing domain")
    void shouldApplyUpdate() {
        // Given
        UserDomain domain = new UserDomain(
                new UserId(UUID.randomUUID()),
                new UserNames("John", "Doe", null),
                new UserEmail("john@example.com"),
                new UserPassword("pass1234567"),
                new AccountStatus(true, true, true, true, Status.ACTIVO),
                new HashSet<>());

        UpdateUserRequest request = new UpdateUserRequest("Jane", null, null, null, null);

        // When
        UserMapper.applyUpdate(domain, request);

        // Then
        assertEquals("Jane", domain.getNames().name());
        assertEquals("Doe", domain.getNames().lastName()); // unchanged
    }

    @Test
    @DisplayName("Given UserDomain, When toResponse, Then returns valid UserResponse")
    void shouldMapDomainToResponse() {
        // Given
        UserDomain domain = new UserDomain(
                new UserId(UUID.randomUUID()),
                new UserNames("John", "Doe", null),
                new UserEmail("john@example.com"),
                new UserPassword("pass1234567"),
                new AccountStatus(true, true, true, true, Status.ACTIVO),
                new HashSet<>());

        // When
        UserResponse response = UserMapper.toResponse(domain);

        // Then
        assertNotNull(response);
        assertEquals("John Doe", response.names());
        assertEquals("john@example.com", response.email());
        assertTrue(response.isEnabled());
    }
}
