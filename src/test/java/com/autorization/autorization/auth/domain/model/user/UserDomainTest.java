package com.autorization.autorization.auth.domain.model.user;

import com.autorization.autorization.auth.domain.exception.RoleAlreadyAssignedException;
import com.autorization.autorization.auth.domain.exception.RoleNotAssignedException;
import com.autorization.autorization.auth.domain.model.role.RoleDomain;
import com.autorization.autorization.auth.domain.model.role.vo.RoleId;
import com.autorization.autorization.auth.domain.model.role.vo.RoleName;
import com.autorization.autorization.auth.domain.model.user.vo.*;
import com.autorization.autorization.shared.domain.exception.NullValueException;
import com.autorization.autorization.shared.domain.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDomainTest {

    private UserDomain user;
    private RoleDomain role;

    @BeforeEach
    void setUp() {
        UserId userId = new UserId(UUID.randomUUID());
        UserNames names = new UserNames("John", "Doe", null);
        UserEmail email = new UserEmail("john.doe@example.com");
        UserPassword password = new UserPassword("securePass123");
        AccountStatus status = new AccountStatus(true, true, true, true, Status.ACTIVO);

        user = new UserDomain(userId, names, email, password, status, new HashSet<>());

        role = new RoleDomain(new RoleId(UUID.randomUUID()), new RoleName("ADMIN"), null, new HashSet<>(),
                Status.ACTIVO);
    }

    @Test
    @DisplayName("Given valid new role, When addRole, Then role is added to user")
    void shouldAddRoleWhenValid() {
        // Given
        assertFalse(user.getRoles().contains(role));

        // When
        user.addRole(role);

        // Then
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    @DisplayName("Given existing role, When addRole, Then throws RoleAlreadyAssignedException")
    void shouldThrowExceptionWhenRoleAlreadyAssigned() {
        // Given
        user.addRole(role);

        // When & Then
        assertThrows(RoleAlreadyAssignedException.class, () -> user.addRole(role));
    }

    @Test
    @DisplayName("Given null role, When addRole, Then throws NullValueException")
    void shouldThrowExceptionWhenAddNullRole() {
        // When & Then
        assertThrows(NullValueException.class, () -> user.addRole(null));
    }

    @Test
    @DisplayName("Given assigned role, When removeRole, Then role is removed")
    void shouldRemoveRoleWhenAssigned() {
        // Given
        user.addRole(role);

        // When
        user.removeRole(role);

        // Then
        assertFalse(user.getRoles().contains(role));
    }

    @Test
    @DisplayName("Given unassigned role, When removeRole, Then throws RoleNotAssignedException")
    void shouldThrowExceptionWhenRemovingUnassignedRole() {
        // Given - role not added

        // When & Then
        assertThrows(RoleNotAssignedException.class, () -> user.removeRole(role));
    }

    @Test
    @DisplayName("Given new email, When changeEmail, Then email is updated")
    void shouldChangeEmail() {
        // Given
        UserEmail newEmail = new UserEmail("new.email@example.com");

        // When
        user.changeEmail(newEmail);

        // Then
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    @DisplayName("Given null userId, When constructor called, Then throws NullValueException")
    void shouldThrowExceptionWhenUserIdNull() {
        assertThrows(NullValueException.class, () -> new UserDomain(
                null,
                new UserNames("John", "Doe", null),
                new UserEmail("test@test.com"),
                null,
                null,
                Collections.emptySet()));
    }
}
