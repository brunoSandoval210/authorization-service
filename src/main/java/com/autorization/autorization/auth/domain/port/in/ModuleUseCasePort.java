package com.autorization.autorization.auth.domain.port.in;

import com.autorization.autorization.auth.adapter.in.web.request.CreateModuleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateModuleRequest;
import com.autorization.autorization.auth.application.dto.out.ModuleResponse;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleUseCasePort {
    ModuleResponse create(CreateModuleRequest request);
    ModuleResponse update(UUID id, UpdateModuleRequest request);
    void deactivate(UUID id);
    void activate(UUID id);
    Optional<ModuleResponse> findById(UUID id);
    List<ModuleResponse> findAll();
    PaginatedResponse<ModuleResponse> search(String name, int page, int size);
}
