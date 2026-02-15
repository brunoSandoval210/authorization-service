package com.autorization.autorization.auth.adapter.in.web.controller.rest;

import com.autorization.autorization.auth.adapter.in.web.request.CreateModuleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateModuleRequest;
import com.autorization.autorization.auth.application.dto.out.ModuleResponse;
import com.autorization.autorization.auth.domain.port.in.ModuleUseCasePort;
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

@WebMvcTest(ModuleController.class)
class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModuleUseCasePort moduleUseCasePort;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private LogControlService logControlService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("given valid request when create module then returns 201")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldCreateModule() throws Exception {
        CreateModuleRequest request = new CreateModuleRequest("Users", "/users", "icon");
        ModuleResponse response = new ModuleResponse(UUID.randomUUID(), "Users", "/users", "icon", "ACTIVO");

        given(moduleUseCasePort.create(any(CreateModuleRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/modules")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Users"));
    }

    @Test
    @DisplayName("given valid request when update module then returns 200")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldUpdateModule() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateModuleRequest request = new UpdateModuleRequest("New Users", "/new", "new-icon");
        ModuleResponse response = new ModuleResponse(id, "New Users", "/new", "new-icon", "ACTIVO");

        given(moduleUseCasePort.update(eq(id), any(UpdateModuleRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/modules/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Users"));
    }

    @Test
    @DisplayName("should find module by id")
    @WithMockUser(username = "admin", authorities = { "READ_PRIVILEGES" })
    void shouldFindById() throws Exception {
        UUID id = UUID.randomUUID();
        ModuleResponse response = new ModuleResponse(id, "Users", "/users", "icon", "ACTIVO");

        given(moduleUseCasePort.findById(id)).willReturn(java.util.Optional.of(response));

        mockMvc.perform(get("/api/modules/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Users"));
    }

    @Test
    @DisplayName("should deactivate module")
    @WithMockUser(username = "admin", authorities = { "WRITE_PRIVILEGES" })
    void shouldDeactivateModule() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/api/modules/{id}/deactivate", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
