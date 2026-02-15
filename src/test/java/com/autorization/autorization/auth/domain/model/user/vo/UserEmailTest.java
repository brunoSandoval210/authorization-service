package com.autorization.autorization.auth.domain.model.user.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserEmailTest {

    @Test
    @DisplayName("Given valid email, When constructor called, Then VO is created")
    void shouldCreateUserEmailWhenValid() {
        // Given
        String validEmail = "test@example.com";

        // When & Then
        assertDoesNotThrow(() -> new UserEmail(validEmail));
    }

    @Test
    @DisplayName("Given invalid email, When constructor called, Then throws IllegalArgumentException")
    void shouldThrowExceptionWhenInvalid() {
        // Given
        String invalidEmail = "invalid-email";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new UserEmail(invalidEmail));
    }

    @Test
    @DisplayName("Given null email, When constructor called, Then throws IllegalArgumentException")
    void shouldThrowExceptionWhenNull() {
        // Given
        String nullEmail = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> new UserEmail(nullEmail));
    }
}
