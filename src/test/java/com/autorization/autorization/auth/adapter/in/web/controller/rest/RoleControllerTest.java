package com.autorization.autorization.auth.adapter.in.web.controller.rest;

import com.autorization.autorization.auth.adapter.in.web.request.CreateRoleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateRoleRequest;
import com.autorization.autorization.auth.application.dto.out.RoleResponse;
import com.autorization.autorization.auth.domain.port.in.RoleUseCasePort;
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

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleUseCasePort roleUseCasePort;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private LogControlService logControlService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("given valid request when create role then returns 201")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldCreateRole() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest("ADMIN", "Administrator");
        RoleResponse response = new RoleResponse(UUID.randomUUID(), "ADMIN", "Administrator", Collections.emptyList(),
                "ACTIVO");

        given(roleUseCasePort.create(any(CreateRoleRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    @DisplayName("given valid request when update role then returns 200")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldUpdateRole() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateRoleRequest request = new UpdateRoleRequest("NEW_ADMIN", "New Description");
        RoleResponse response = new RoleResponse(id, "NEW_ADMIN", "New Description", Collections.emptyList(), "ACTIVO");

        given(roleUseCasePort.update(eq(id), any(UpdateRoleRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/roles/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NEW_ADMIN"));
    }

    @Test
    @DisplayName("should deactivate role")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldDeactivateRole() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/api/roles/{id}/deactivate", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should find role by id")
    @WithMockUser(username = "admin", authorities = { "READ_PRIVILEGES" })
    void shouldFindById() throws Exception {
        UUID id = UUID.randomUUID();
        RoleResponse response = new RoleResponse(id, "ADMIN", "Desc", Collections.emptyList(), "ACTIVO");

        given(roleUseCasePort.findById(id)).willReturn(java.util.Optional.of(response));

        mockMvc.perform(get("/api/roles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }
}
