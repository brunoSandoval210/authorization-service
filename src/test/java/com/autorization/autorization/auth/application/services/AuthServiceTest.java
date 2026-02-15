package com.autorization.autorization.auth.application.services;

import com.autorization.autorization.auth.adapter.in.web.request.LoginRequest;
import com.autorization.autorization.auth.application.dto.out.AuthResponse;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.*;
import com.autorization.autorization.auth.domain.port.out.UserRepositoryPort;
import com.autorization.autorization.security.util.JwtUtil;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("should login successfully")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        UserDomain user = createUser("test@test.com", "encodedPassword", Status.ACTIVO);

        given(userRepositoryPort.findByEmail(any(UserEmail.class))).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtUtil.generateToken(any(UserDomain.class))).willReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    @DisplayName("should throw BadCredentials when user not found")
    void shouldThrowBadCredentialsWhenUserNotFound() {
        LoginRequest request = new LoginRequest("test@test.com", "password");

        given(userRepositoryPort.findByEmail(any(UserEmail.class))).willReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("should throw BadCredentials when password does not match")
    void shouldThrowBadCredentialsWhenPasswordMismatch() {
        LoginRequest request = new LoginRequest("test@test.com", "wrong");
        UserDomain user = createUser("test@test.com", "encodedPassword", Status.ACTIVO);

        given(userRepositoryPort.findByEmail(any(UserEmail.class))).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("should throw DisabledException when account is inactive")
    void shouldThrowDisabledException() {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        UserDomain user = createUser("test@test.com", "encodedPassword", Status.INACTIVO);

        given(userRepositoryPort.findByEmail(any(UserEmail.class))).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        assertThrows(DisabledException.class, () -> authService.login(request));
    }

    private UserDomain createUser(String email, String password, Status status) {
        return new UserDomain(
                new UserId(UUID.randomUUID()),
                new UserNames("Test", "User", null),
                new UserEmail(email),
                new UserPassword(password),
                new AccountStatus(status == Status.ACTIVO, true, true, true, status),
                new HashSet<>());
    }
}
