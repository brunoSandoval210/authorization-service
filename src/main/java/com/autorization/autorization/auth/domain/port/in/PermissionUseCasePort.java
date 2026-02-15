package com.autorization.autorization.auth.domain.port.in;

import com.autorization.autorization.auth.adapter.in.web.request.CreatePermissionRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdatePermissionRequest;
import com.autorization.autorization.auth.application.dto.out.PermissionResponse;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionUseCasePort {
    PermissionResponse create(CreatePermissionRequest request);
    PermissionResponse update(UUID id, UpdatePermissionRequest request);
    void deactivate(UUID id);
    void activate(UUID id) ;
    Optional<PermissionResponse> findById(UUID id);
    List<PermissionResponse> findAll();
    PaginatedResponse<PermissionResponse> search(String name, int page, int size);
}
