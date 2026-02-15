package com.autorization.autorization.auth.domain.port.in;

import com.autorization.autorization.auth.adapter.in.web.request.CreateRoleRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateRoleRequest;
import com.autorization.autorization.auth.application.dto.out.RoleResponse;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleUseCasePort {
    RoleResponse create(CreateRoleRequest request);
    RoleResponse update(UUID id, UpdateRoleRequest request);
    void deactivate(UUID id);
    void activate(UUID id);
    Optional<RoleResponse> findById(UUID id);
    List<RoleResponse> findAll();
    void addPermission(UUID roleId, UUID permissionId);
    void removePermission(UUID roleId, UUID permissionId);
    PaginatedResponse<RoleResponse> search(String name, int page, int size);
}
