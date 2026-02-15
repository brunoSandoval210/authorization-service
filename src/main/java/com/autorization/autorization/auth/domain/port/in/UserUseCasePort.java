package com.autorization.autorization.auth.domain.port.in;

import com.autorization.autorization.auth.adapter.in.web.request.CreateUserRequest;
import com.autorization.autorization.auth.adapter.in.web.request.UpdateUserRequest;
import com.autorization.autorization.auth.application.dto.out.UserResponse;
import com.autorization.autorization.shared.application.dto.PaginatedResponse;
import com.autorization.autorization.shared.domain.model.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserUseCasePort {
    UserResponse create(CreateUserRequest request);
    UserResponse update(UUID id, UpdateUserRequest user);
    void deactivate(UUID id);
    void activate(UUID id);
    Optional<UserResponse> findById(UUID id);
    List<UserResponse> findAll();
    void assignRole(UUID userId, UUID roleId);
    void revokeRole(UUID userId, UUID roleId);
    PaginatedResponse<UserResponse> search(String email, Status status, int page, int size);
}
