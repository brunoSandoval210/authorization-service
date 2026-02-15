package com.autorization.autorization.auth.adapter.in.web.controller.rest;

import com.autorization.autorization.auth.adapter.in.web.request.CreatePermissionRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdatePermissionRequest;
import com.autorization.autorization.auth.application.dto.out.PermissionResponse;
import com.autorization.autorization.auth.domain.port.in.PermissionUseCasePort;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PermissionController.class)
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionUseCasePort permissionUseCasePort;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private LogControlService logControlService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("given valid request when create permission then returns 201")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldCreatePermission() throws Exception {
        CreatePermissionRequest request = new CreatePermissionRequest("READ", "Read access");
        PermissionResponse response = new PermissionResponse(UUID.randomUUID(), "READ", "Read access", "ACTIVO");

        given(permissionUseCasePort.create(any(CreatePermissionRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/permissions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("READ"));
    }

    @Test
    @DisplayName("given valid request when update permission then returns 200")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldUpdatePermission() throws Exception {
        UUID id = UUID.randomUUID();
        UpdatePermissionRequest request = new UpdatePermissionRequest("WRITE", "Write access");
        PermissionResponse response = new PermissionResponse(id, "WRITE", "Write access", "ACTIVO");

        given(permissionUseCasePort.update(eq(id), any(UpdatePermissionRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/permissions/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("WRITE"));
    }

    @Test
    @DisplayName("should find permission by id")
    @WithMockUser(username = "admin", authorities = { "READ_PRIVILEGES" })
    void shouldFindById() throws Exception {
        UUID id = UUID.randomUUID();
        PermissionResponse response = new PermissionResponse(id, "READ", "Desc", "ACTIVO");

        given(permissionUseCasePort.findById(id)).willReturn(java.util.Optional.of(response));

        mockMvc.perform(get("/api/permissions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("READ"));
    }

    @Test
    @DisplayName("should deactivate permission")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldDeactivatePermission() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/api/permissions/{id}/deactivate", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
