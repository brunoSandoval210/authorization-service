package com.autorization.autorization.shared.infraestructure.persistence;

import com.autorization.autorization.security.service.AuthPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import com.autorization.autorization.shared.infraestructure.web.RequestIdFilter;

public class SecurityAuditorAware implements AuditorAware<String> {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditorAware.class);

    @Override
    public Optional<String> getCurrentAuditor() {
        String auditor = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                log.debug("SecurityAuditorAware: principal class = {}", principal == null ? "null" : principal.getClass().getName());

                if (principal instanceof AuthPrincipal) {
                    AuthPrincipal ap = (AuthPrincipal) principal;
                    String uid = ap.userId();
                    if (uid != null) uid = uid.trim();
                    if (uid != null && !uid.isBlank()) {
                        auditor = uid;
                    } else {
                        String username = ap.username();
                        if (username != null) username = username.trim();
                        if (username != null && !username.isBlank()) {
                            auditor = username;
                        }
                    }
                } else {
                    String username = null;
                    if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                        username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                    } else if (principal instanceof String) {
                        username = (String) principal;
                    }
                    if (username != null) username = username.trim();
                    if (username != null && !username.isBlank()) {
                        auditor = username;
                    }
                }
            }

            if (auditor == null) {
                // usar la constante centralizada para el MDC key
                String reqId = MDC.get(RequestIdFilter.MDC_KEY);
                if (reqId != null) reqId = reqId.trim();
                if (reqId != null && !reqId.isBlank()) {
                    auditor = reqId;
                }
            }
        } catch (Exception ex) {
            log.error("Error obteniendo auditor actual, se usará fallback 'system': {}", ex.getMessage(), ex);
        }

        if (auditor == null || auditor.isBlank()) {
            auditor = "system";
        }
        log.debug("Auditor final devuelto: {}", auditor);
        return Optional.of(auditor);
    }
}
