package com.autorization.autorization.auth.application.dto.out;

import java.util.List;

public record UserSecurityResponse(String email, String password, List<String> authorities, boolean enabled) {
}
