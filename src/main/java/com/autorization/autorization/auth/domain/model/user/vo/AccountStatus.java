package com.autorization.autorization.auth.domain.model.user.vo;

import com.autorization.autorization.shared.domain.model.Status;

public record AccountStatus(
        boolean isEnabled,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        Status status
) {}