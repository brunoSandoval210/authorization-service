package com.autorization.autorization.auth.domain.model.module.vo;

public record ModulePath(String url) {
    public ModulePath {
        if (url == null || !url.startsWith("/")) {
            throw new IllegalArgumentException("El path debe iniciar con /");
        }
    }
}
