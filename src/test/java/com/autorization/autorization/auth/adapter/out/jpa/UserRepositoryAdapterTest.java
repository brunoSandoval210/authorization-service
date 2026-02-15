package com.autorization.autorization.auth.adapter.out.jpa;

import com.autorization.autorization.auth.adapter.out.jpa.entity.User;
import com.autorization.autorization.auth.adapter.out.jpa.repository.RoleRepository;
import com.autorization.autorization.auth.adapter.out.jpa.repository.UserRepository;
import com.autorization.autorization.auth.domain.model.user.UserDomain;
import com.autorization.autorization.auth.domain.model.user.vo.*;
import com.autorization.autorization.auth.domain.port.out.UserRepositoryPort;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    @DisplayName("Given new user domain, When save, Then saves entity and returns domain")
    void shouldSaveNewUser() {
        // Given
        UserDomain domain = new UserDomain(
                new UserId(UUID.randomUUID()),
                new UserNames("John", "Doe", null),
                new UserEmail("john@example.com"),
                new UserPassword("securePassword123"),
                new AccountStatus(true, true, true, true, Status.ACTIVO),
                new HashSet<>());

        given(userRepository.findById(domain.getUserId().id())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        UserDomain saved = userRepositoryAdapter.save(domain);

        // Then
        assertNotNull(saved);
        assertEquals(domain.getUserId().id(), saved.getUserId().id());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Given existing user id, When findById, Then returns domain")
    void shouldFindById() {
        // Given
        UUID id = UUID.randomUUID();
        User entity = new User();
        entity.setUserId(id);
        entity.setName("John");
        entity.setLastName("Doe");
        entity.setEmail("john@example.com");
        entity.setPassword("securePassword123");
        entity.setStatus(Status.ACTIVO);

        given(userRepository.findById(id)).willReturn(Optional.of(entity));

        // When
        Optional<UserDomain> result = userRepositoryAdapter.findById(new UserId(id));

        // Then
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getNames().name());
    }
}
