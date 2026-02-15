package com.autorization.autorization.auth.domain.port.in;

import com.autorization.autorization.auth.application.dto.out.UserSecurityResponse;

public interface GetUserForAuthUseCase {
    UserSecurityResponse execute(String email);
}
