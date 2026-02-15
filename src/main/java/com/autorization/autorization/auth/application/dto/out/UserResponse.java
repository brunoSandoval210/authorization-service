package com.autorization.autorization.auth.application.dto.out;

import java.util.List;
import java.util.UUID;

public record UserResponse (
        UUID id,
        String names,
        String email,
        boolean isEnabled,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        List<RoleResponse> roles,
        String status
){
}
