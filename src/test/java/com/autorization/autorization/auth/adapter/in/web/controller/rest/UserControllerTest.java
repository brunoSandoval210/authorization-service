package com.autorization.autorization.auth.adapter.in.web.controller.rest;

import com.autorization.autorization.auth.adapter.in.web.request.CreateUserRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateUserRequest;
import com.autorization.autorization.auth.application.dto.out.UserResponse;
import com.autorization.autorization.auth.domain.port.in.UserUseCasePort;
import com.autorization.autorization.security.util.JwtUtil;
import com.autorization.autorization.shared.infraestructure.logging.LogControlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserUseCasePort userUseCasePort;

        @MockitoBean
        private JwtUtil jwtUtil;

        @MockitoBean
        private LogControlService logControlService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Test
        @DisplayName("Given valid request, When create user, Then returns 201 Created and UserResponse")
        @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
        void shouldCreateUser() throws Exception {
                // Given
                CreateUserRequest request = new CreateUserRequest("John", "Doe", null, "john.doe@example.com",
                                "pass1234567");
                UserResponse response = new UserResponse(
                                UUID.randomUUID(), "John Doe", "john.doe@example.com", true, true, true, true,
                                Collections.emptyList(),
                                "ACTIVO");

                given(userUseCasePort.create(any(CreateUserRequest.class))).willReturn(response);

                // When & Then
                mockMvc.perform(post("/api/users")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                                .andExpect(jsonPath("$.names").value("John Doe"));
        }

        @Test
        @DisplayName("Given invalid request, When create user, Then returns 400 Bad Request")
        @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
        void shouldReturnBadRequestWhenInvalid() throws Exception {
                // Given - empty name which violates validation (assuming @NotBlank is on name)
                CreateUserRequest request = new CreateUserRequest("", "Doe", null, "john.doe@example.com",
                                "pass1234567");

                // When & Then
                mockMvc.perform(post("/api/users")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Given valid id and request, When update user, Then returns 200 OK")
        @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
        void shouldUpdateUser() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();
                UpdateUserRequest request = new UpdateUserRequest("Jane", null, null, null, null);
                UserResponse response = new UserResponse(
                                userId, "Jane Doe", "john.doe@example.com", true, true, true, true,
                                Collections.emptyList(), "ACTIVO");

                given(userUseCasePort.update(eq(userId), any(UpdateUserRequest.class))).willReturn(response);

                // When & Then
                mockMvc.perform(put("/api/users/{id}", userId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.names").value("Jane Doe"));
        }

        @Test
        @DisplayName("Given valid id, When deactivate, Then returns 204 No Content")
        @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
        void shouldDeactivateUser() throws Exception {
                // Given
                UUID userId = UUID.randomUUID();

                // When & Then
                mockMvc.perform(patch("/api/users/{id}/deactivate", userId)
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }
}