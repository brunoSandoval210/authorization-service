package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.CreateUserRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateUserRequest;
import com.autorization.autorization.auth.application.dto.out.UserResponse;
import com.autorization.autorization.auth.domain.exception.UserAlreadyExistsException;
import com.autorization.autorization.auth.domain.exception.UserNotFoundException;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.UserEmail;
import com.autorization.autorization.auth.domain.model.user.vo.UserId;
import com.autorization.autorization.auth.domain.port.out.RoleRepositoryPort;
import com.autorization.autorization.auth.domain.port.out.UserRepositoryPort;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Given valid request, When create, Then user is saved and response returned")
    void shouldCreateUserWhenValid() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "Doe", null, "john.doe@example.com",
                "securePassword123");
        given(userRepositoryPort.findByEmail(any(UserEmail.class))).willReturn(Optional.empty());
        given(passwordEncoder.encode("securePassword123")).willReturn("encodedPass");
        given(userRepositoryPort.save(any(UserDomain.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        UserResponse response = userService.create(request);

        // Then
        assertNotNull(response);
        assertEquals("john.doe@example.com", response.email());

        ArgumentCaptor<UserDomain> userCaptor = ArgumentCaptor.forClass(UserDomain.class);
        verify(userRepositoryPort).save(userCaptor.capture());

        UserDomain capturedUser = userCaptor.getValue();
        assertEquals("John", capturedUser.getNames().name());
        assertEquals("encodedPass", capturedUser.getPassword().value());
    }

    @Test
    @DisplayName("Given existing email, When create, Then throws UserAlreadyExistsException")
    void shouldThrowExceptionWhenEmailExistsOnCreate() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "Doe", null, "john.doe@example.com",
                "securePassword123");
        given(userRepositoryPort.findByEmail(any(UserEmail.class))).willReturn(Optional.of(new UserDomain(
                new UserId(UUID.randomUUID()),
                new com.autorization.autorization.auth.domain.model.user.vo.UserNames("John", "Doe", null),
                new UserEmail("john.doe@example.com"),
                new com.autorization.autorization.auth.domain.model.user.vo.UserPassword("securePassword123"),
                new com.autorization.autorization.auth.domain.model.user.vo.AccountStatus(true, true, true, true,
                        Status.ACTIVO),
                new HashSet<>())));

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(request));
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Given valid update request, When update, Then user is updated")
    void shouldUpdateUserWhenValid() {
        // Given
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("Jane", null, null, null, null); // only name update

        UserDomain existingUser = new UserDomain(
                new UserId(userId),
                new com.autorization.autorization.auth.domain.model.user.vo.UserNames("John", "Doe", null),
                new UserEmail("john.doe@example.com"),
                new com.autorization.autorization.auth.domain.model.user.vo.UserPassword("securePassword123"),
                new com.autorization.autorization.auth.domain.model.user.vo.AccountStatus(true, true, true, true,
                        Status.ACTIVO),
                new HashSet<>());

        given(userRepositoryPort.findById(new UserId(userId))).willReturn(Optional.of(existingUser));
        given(userRepositoryPort.save(any(UserDomain.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        UserResponse response = userService.update(userId, request);

        // Then
        assertEquals("Jane", response.names().split(" ")[0]);
        verify(userRepositoryPort).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Given non-existent user, When update, Then throws UserNotFoundException")
    void shouldThrowExceptionWhenUserNotFoundOnUpdate() {
        // Given
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("Jane", null, null, null, null);
        given(userRepositoryPort.findById(new UserId(userId))).willReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.update(userId, request));
    }

    @Test
    @DisplayName("Given search filters, When search, Then returns paginated response")
    void shouldSearchUsers() {
        // Given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<UserDomain> users = Collections.singletonList(new UserDomain(
                new UserId(UUID.randomUUID()),
                new com.autorization.autorization.auth.domain.model.user.vo.UserNames("John", "Doe", null),
                new UserEmail("john@example.com"),
                null,
                new com.autorization.autorization.auth.domain.model.user.vo.AccountStatus(true, true, true, true,
                        Status.ACTIVO),
                new HashSet<>()));
        Page<UserDomain> userPage = new PageImpl<>(users, pageable, 1);

        given(userRepositoryPort.searchByUsernameOrUserId(null, null, pageable)).willReturn(userPage);

        // When
        PaginatedResponse<UserResponse> response = userService.search(null, null, page, size);

        // Then
        assertEquals(1, response.totalElements());
        assertEquals(1, response.content().size());
        assertEquals("john@example.com", response.content().get(0).email());
    }
}
