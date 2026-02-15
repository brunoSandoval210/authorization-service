package com.autorization.autorization.auth.adapter.in.web.request;

import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
        @Size(max = 100)
        String name,
        @Size(max = 200)
        String description
) {}
